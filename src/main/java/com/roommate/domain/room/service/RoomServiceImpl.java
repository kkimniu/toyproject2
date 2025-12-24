package com.roommate.domain.room.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.favorite.repository.FavoriteRepository;
import com.roommate.domain.member.service.MemberService;
import com.roommate.domain.room.dto.request.RoomCreateRequest;
import com.roommate.domain.room.dto.request.RoomStatusUpdateRequest;
import com.roommate.domain.room.dto.request.RoomUpdateRequest;
import com.roommate.domain.room.dto.response.MyRoomListItemResponse;
import com.roommate.domain.room.dto.response.RoomDetailResponse;
import com.roommate.domain.room.dto.response.RoomMapItemResponse;
import com.roommate.domain.room.entity.RoomDetailEntity;
import com.roommate.domain.room.entity.RoomEntity;
import com.roommate.domain.room.entity.RoomMapItemEntity;
import com.roommate.domain.room.entity.RoomStatusEnum;
import com.roommate.domain.room.repository.RoomRepository;
import com.roommate.external.kakao.dto.KakaoGeoPoint;
import com.roommate.external.kakao.service.KakaoMapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final MemberService memberService;

    private static final String KAKAO_MAP_BASE = "https://map.kakao.com/link";

    private final RoomRepository roomRepository;
    private final KakaoMapService kakaoMapService;
    private final FavoriteRepository favoriteRepository;
    private final RoomImageService roomImageService;


    /**
     * 공통: 방 조회 + 소유자(memberId) 검증
     * <p>
     * 왜 이렇게 했는지:
     * - changeStatus / updateRoom / deleteRoom 에서
     * 동일한 패턴(존재 여부 + 작성자 권한 체크)이 반복돼서
     * 중복 코드를 제거하고, 정책 변경 시 한 곳만 수정 가능하게 함.
     */
    private RoomEntity getOwnedRoomOrThrow(Long roomId, Long memberId) {
        RoomEntity roomEntity = roomRepository.findById(roomId);
        if (roomEntity == null) {
            throw new ApiException(ErrorCode.ROOM_NOT_FOUND);
        }
        if (!roomEntity.getMemberId().equals(memberId)) {
            throw new ApiException(ErrorCode.ROOM_ACCESS_DENIED);
        }
        return roomEntity;
    }

    /**
     * 좌표 보완 로직
     * - lat/lng가 이미 있으면 그대로 사용
     * - 없고 address 가 있으면 Kakao 지오코딩으로 좌표 조회
     * - 둘 다 없으면 INVALID_ROOM_LOCATION 예외
     * <p>
     * 왜 이렇게 했는지:
     * - createRoom 내부 if/else 로직을 메서드로 캡슐화해서
     * "좌표 보완" 이라는 의도가 더 잘 보이게 함.
     */
    private KakaoGeoPoint resolveGeoPoint(String address) {
        if (address == null || address.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_ROOM_LOCATION);
        }

        // 외부 API 호출 실패 시 kakaoMapService 내부에서 ApiException 등으로 래핑하도록 설계
        return kakaoMapService.geocodeAddress(address);
    }

    /**
     * RoomCreateRequest -> RoomEntity 변환
     * <p>
     * 왜 이렇게 했는지:
     * - DTO -> Entity 변환 로직이 Service 여기저기에 흩어지지 않도록
     * private 메서드로 모아두어, 필드 추가/변경 시 실수 가능성을 줄임.
     * - Entity 쪽에 정적 팩토리를 두는 것도 좋은데,
     * 기존 코드를 최소 변경하면서 리팩터링하기 위해 Service에 둠.
     */
    private RoomEntity toRoomEntity(RoomCreateRequest request, Long memberId, KakaoGeoPoint point) {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setMemberId(memberId);
        roomEntity.setRoomTitle(request.getTitle());
        roomEntity.setRoomContent(request.getContent());
        roomEntity.setRoomTypeId(request.getRoomTypeId());
        roomEntity.setMonthlyRent(request.getMonthlyRent());
        roomEntity.setDeposit(request.getDeposit());
        roomEntity.setAreaM2(request.getAreaM2());
        roomEntity.setFloor(request.getFloor());
        roomEntity.setAddress(request.getAddress());
        roomEntity.setLegalDong(request.getLegalDong());
        roomEntity.setLandNumber(request.getLandNumber());
        roomEntity.setLat(point.getLat());
        roomEntity.setLng(point.getLng());
        roomEntity.setAvailableFrom(request.getAvailableFrom());
        roomEntity.setMaxRoommates(request.getMaxRoommates());
        roomEntity.setStatus(RoomStatusEnum.OPEN); // 처음은 OPEN
        return roomEntity;
    }

    /**
     * RoomUpdateRequest 내용을 기존 RoomEntity에 반영
     * <p>
     * 왜 이렇게 했는지:
     * - updateRoom 메서드에서 setter 호출이 길게 나열되는 것을 줄이고
     * "업데이트 적용"이라는 목적이 보이도록 캡슐화.
     * - 필드가 추가/변경되더라도 한 곳만 수정하면 됨.
     */
    private void applyUpdate(RoomEntity roomEntity, RoomUpdateRequest request) {
        KakaoGeoPoint point = resolveGeoPoint(request.getAddress());
        roomEntity.setRoomTitle(request.getTitle());
        roomEntity.setRoomContent(request.getContent());
        roomEntity.setRoomTypeId(request.getRoomTypeId());
        roomEntity.setMonthlyRent(request.getMonthlyRent());
        roomEntity.setDeposit(request.getDeposit());
        roomEntity.setAreaM2(request.getAreaM2());
        roomEntity.setFloor(request.getFloor());
        roomEntity.setAddress(request.getAddress());
        roomEntity.setLegalDong(request.getLegalDong());
        roomEntity.setLandNumber(request.getLandNumber());
        roomEntity.setLat(point.getLat());
        roomEntity.setLng(point.getLng());
        roomEntity.setAvailableFrom(request.getAvailableFrom());
        roomEntity.setMaxRoommates(request.getMaxRoommates());
    }

    /**
     * Kakao 지도 딥링크 URL 생성
     * <p>
     * 왜 이렇게 했는지:
     * - getRoomDetail 내부에서 문자열 조합이 중복되어
     * URL 포맷이 바뀌었을 때 여러 군데를 수정해야 하는 문제를 방지함.
     * - title 인코딩, 기본값 처리 등을 한 곳에 모아서 버그 가능성을 줄임.
     */
    private String buildKakaoMapUrl(String path, String title, double lat, double lng) {
        String safeTitle = title != null ? title : "룸메이트";
        String encodedTitle = UriUtils.encode(safeTitle, StandardCharsets.UTF_8);
        return KAKAO_MAP_BASE + "/" + path + "/" + encodedTitle + "," + lat + "," + lng;
    }

    @Override
    @Transactional
    public Long createRoom(RoomCreateRequest roomCreateRequest, Long memberId) {

        KakaoGeoPoint point = resolveGeoPoint(roomCreateRequest.getAddress());
        RoomEntity roomEntity = toRoomEntity(roomCreateRequest, memberId, point);

        roomRepository.insertRoom(roomEntity);

        roomImageService.attachTempImagesToRoom(roomEntity.getRoomId(), memberId, roomCreateRequest.getTempFileIds());

        return roomEntity.getRoomId();
    }

    @Override
    @Transactional
    public void changeStatus(Long roomId, RoomStatusUpdateRequest roomStatusUpdateRequest, Long memberId) {
        // 존재 여부 + 작성자 권한 검증 공통 메서드 사용
        getOwnedRoomOrThrow(roomId, memberId);

        roomRepository.updateRoomStatus(roomId, memberId, roomStatusUpdateRequest.getStatus());
    }

    @Override
    @Transactional
    public void updateRoom(Long roomId, RoomUpdateRequest roomUpdateRequest, Long memberId) {
        // 왜: 수정 권한은 작성자 본인만 → 공통 메서드 이용
        RoomEntity roomEntity = getOwnedRoomOrThrow(roomId, memberId);

        // 필드 업데이트 적용
        applyUpdate(roomEntity, roomUpdateRequest);

        roomRepository.updateRoom(roomEntity);

        roomImageService.updateRoomImages(roomId, memberId, roomUpdateRequest.getImageUrls(), roomUpdateRequest.getTempFileIds());
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId, Long memberId) {
        // 존재 여부 + 권한 체크
        getOwnedRoomOrThrow(roomId, memberId);

        // 왜: 정책상 soft delete + CLOSED로 처리
        roomRepository.softDeleteRoom(roomId, memberId);
    }


    @Override
    @Transactional(readOnly = true)
    public RoomDetailResponse getRoomDetail(Long roomId, Long currentMemberId) {
        RoomDetailEntity roomDetailEntity = roomRepository.findDetailById(roomId);
        if (roomDetailEntity == null) {
            throw new ApiException(ErrorCode.ROOM_NOT_FOUND);
        }
        Long ownerId = roomDetailEntity.getOwnerId();
        List<String> ownerTags = memberService.getMemberTags(ownerId);

        // 이미지 리스트 추가
        List<String> imageUrls = roomImageService.findImageUrlsByRoomId(roomId);

        String kakaoMapUrl = null;
        String kakaoDirectionUrl = null;
        String kakaoRoadviewUrl = null;

        // Kakao 딥링크 생성
        if (roomDetailEntity.getLat() != null && roomDetailEntity.getLng() != null) {
            double lat = roomDetailEntity.getLat();
            double lng = roomDetailEntity.getLng();
            String title = roomDetailEntity.getTitle();

            kakaoMapUrl = buildKakaoMapUrl("map", title, lat, lng);
            kakaoDirectionUrl = buildKakaoMapUrl("to", title, lat, lng);
            kakaoRoadviewUrl = KAKAO_MAP_BASE + "/roadview/" + lat + "," + lng;
        }

        boolean favorited = false;
        if (currentMemberId != null) {
            int exists = favoriteRepository.existsByMemberIdAndRoomId(currentMemberId, roomId);
            favorited = (exists > 0);
        }

        return new RoomDetailResponse(
                roomDetailEntity.getRoomId(),
                roomDetailEntity.getTitle(),
                roomDetailEntity.getContent(),
                roomDetailEntity.getRoomTypeId(),
                roomDetailEntity.getRoomTypeName(),
                roomDetailEntity.getMonthlyRent(),
                roomDetailEntity.getDeposit(),
                roomDetailEntity.getAreaM2(),
                roomDetailEntity.getFloor(),
                roomDetailEntity.getAddress(),
                roomDetailEntity.getLegalDong(),
                roomDetailEntity.getLandNumber(),
                roomDetailEntity.getLat(),
                roomDetailEntity.getLng(),
                roomDetailEntity.getAvailableFrom(),
                roomDetailEntity.getMaxRoommates(),
                roomDetailEntity.getViews(),
                roomDetailEntity.getInterestCount(),
                roomDetailEntity.getStatus(),
                roomDetailEntity.getRoomCreatedAt(),
                roomDetailEntity.getRoomUpdatedAt(),
                roomDetailEntity.getOwnerId(),
                roomDetailEntity.getOwnerName(),
                roomDetailEntity.getOwnerPhotoUrl(),
                ownerTags,
                imageUrls,
                favorited,
                kakaoMapUrl,
                kakaoDirectionUrl,
                kakaoRoadviewUrl
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomMapItemResponse> getRoomsForMap(double north, double south, double east, double west, int zoom) {
        log.info("[RoomService] getRoomsForMap north={}, south={}, east={}, west={}, zoom={}",
                north, south, east, west, zoom);
        List<RoomMapItemEntity> roomMapItemEntities = roomRepository.findForMap(north, south, east, west, zoom);
        log.info("[RoomService] getRoomsForMap result size={}", roomMapItemEntities.size());
        roomMapItemEntities.forEach(item ->
                log.info("[RoomService] map item roomId={}, lat={}, lng={}",
                        item.getRoomId(), item.getLat(), item.getLng())
        );

        return roomMapItemEntities.stream().
                map(roomMapItemEntity -> new RoomMapItemResponse(
                        roomMapItemEntity.getRoomId(),
                        roomMapItemEntity.getLat(),
                        roomMapItemEntity.getLng(),
                        roomMapItemEntity.getMonthlyRent(),
                        roomMapItemEntity.getDeposit(),
                        roomMapItemEntity.getStatus(),
                        roomMapItemEntity.getThumbnailUrl())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyRoomListItemResponse> getMyRooms(Long memberId) {
        return roomRepository.findMyRooms(memberId);
    }
}
