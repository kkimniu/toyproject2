package com.roommate.domain.member.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.auth.repository.TokenRefreshRepository;
import com.roommate.domain.chat.repository.ChatMessageRepository;
import com.roommate.domain.chat.repository.ChatRoomRepository;
import com.roommate.domain.favorite.repository.FavoriteRepository;
import com.roommate.domain.member.dto.request.MemberProfileUpdateRequest;
import com.roommate.domain.member.dto.response.*;
import com.roommate.domain.member.entity.*;
import com.roommate.domain.member.repository.*;
import com.roommate.domain.notification.repository.NotificationRepository;
import com.roommate.domain.room.repository.RoomRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final WorkTypeRepository workTypeRepository;
    private final HobbyRepository hobbyRepository;
    private final PreferenceRepository preferenceRepository;
    private final PetRepository petRepository;

    private final MemberHobbyRepository memberHobbyRepository;
    private final MemberPreferenceRepository memberPreferenceRepository;
    private final MemberPetRepository memberPetRepository;

    private final FavoriteRepository favoriteRepository;
    private final NotificationRepository notificationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomRepository roomRepository;
    private final TokenRefreshRepository tokenRefreshRepository;

    private WorkTypeResponse toWorkTypeResponse(WorkTypeEntity workTypeEntity) {
        WorkTypeResponse workTypeResponse = new WorkTypeResponse();
        workTypeResponse.setWorkTypeId(workTypeEntity.getWorkTypeId());
        workTypeResponse.setWorkTypeName(workTypeEntity.getWorkTypeName());
        return workTypeResponse;
    }

    private HobbyResponse toHobbyResponse(HobbyEntity hobbyEntity) {
        HobbyResponse hobbyResponse = new HobbyResponse();
        hobbyResponse.setHobbyId(hobbyEntity.getHobbyId());
        hobbyResponse.setHobbyName(hobbyEntity.getHobbyName());
        return hobbyResponse;
    }

    private PreferenceResponse toPreferenceResponse(PreferenceEntity preferenceEntity) {
        PreferenceResponse preferenceResponse = new PreferenceResponse();
        preferenceResponse.setPreferenceId(preferenceEntity.getPreferenceId());
        preferenceResponse.setPreferenceName(preferenceEntity.getPreferenceName());
        return preferenceResponse;
    }

    private PetResponse toPetResponse(PetEntity petEntity) {
        PetResponse petResponse = new PetResponse();
        petResponse.setPetId(petEntity.getPetId());
        petResponse.setPetName(petEntity.getPetName());
        return petResponse;
    }

    private MemberResponse toMemberResponse(MemberEntity memberEntity, WorkTypeEntity workTypeEntity, List<HobbyEntity> hobbyEntities, List<PreferenceEntity> preferenceEntities, List<PetEntity> petEntities) {
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(memberEntity.getMemberId());
        memberResponse.setEmail(memberEntity.getEmail());
        memberResponse.setName(memberEntity.getName());
        memberResponse.setPhone(memberEntity.getPhone());
        memberResponse.setPhotoUrl(memberEntity.getPhotoUrl());
        if (workTypeEntity != null) {
            memberResponse.setWorkTypeId(workTypeEntity.getWorkTypeId());
            memberResponse.setWorkTypeName(workTypeEntity.getWorkTypeName());
        }
        memberResponse.setSleepTime(memberEntity.getSleepTime());
        memberResponse.setSmoking(memberEntity.getSmoking() != null ? memberEntity.getSmoking() : MemberSmokingEnum.NON_SMOKER);
        memberResponse.setDrinking(memberEntity.getDrinking() != null ? memberEntity.getDrinking() : MemberDrinkingEnum.NONE);
        memberResponse.setMbti(memberEntity.getMbti());
        memberResponse.setMemberRoleEnum(memberEntity.getRole());
        memberResponse.setHobbies(hobbyEntities.stream().map(this::toHobbyResponse).toList());
        memberResponse.setPreferences(preferenceEntities.stream().map(this::toPreferenceResponse).toList());
        memberResponse.setPets(petEntities.stream().map(this::toPetResponse).toList());
        return memberResponse;
    }

    @Override
    public FormCodesResponse getFormCodes() {
        FormCodesResponse formCodesResponse = new FormCodesResponse();
        formCodesResponse.setWorkTypes(workTypeRepository.findAll().stream().map(this::toWorkTypeResponse).toList());
        formCodesResponse.setHobbies(hobbyRepository.findAll().stream().map(this::toHobbyResponse).toList());
        formCodesResponse.setPreferences(preferenceRepository.findAll().stream().map(this::toPreferenceResponse).toList());
        formCodesResponse.setPets(petRepository.findAll().stream().map(this::toPetResponse).toList());
        return formCodesResponse;
    }

    @Override
    public MemberResponse memberInfo(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        if (memberEntity.getDeleted() == 1) {
            // 왜 이렇게 썼는지:
            // - 탈퇴한 사용자가 토큰을 들고 있을 수도 있으므로,
            //   deleted 플래그가 1이면 로그인 불가 상태로 취급.
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }
        WorkTypeEntity workTypeEntity = null;
        if (memberEntity.getWorkTypeId() != null) {
            workTypeEntity = workTypeRepository.findById(memberEntity.getWorkTypeId()).orElse(null); // 없다고 해서 에러로 처리하진 않음
        }
        List<HobbyEntity> hobbyEntities = hobbyRepository.findByMemberId(memberId);
        List<PreferenceEntity> preferenceEntities = preferenceRepository.findByMemberId(memberId);
        List<PetEntity> petEntities = petRepository.findByMemberId(memberId);
        MemberResponse memberResponse = toMemberResponse(memberEntity, workTypeEntity, hobbyEntities, preferenceEntities, petEntities);
        return memberResponse;
    }

    @Override
    @Transactional
    public MemberResponse updateMemberProfile(Long memberId, MemberProfileUpdateRequest memberProfileUpdateRequest) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        if (memberEntity.getDeleted() == 1) {
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }

        // 1) 단일 필드 업데이트
        // 왜 이렇게 썼는지:
        // - null 체크 후 값이 있을 때만 업데이트 해서
        //   부분 업데이트(PATCH와 같은 효과)를 허용하기 위함.
        if (memberProfileUpdateRequest.getName() != null) {
            memberEntity.setName(memberProfileUpdateRequest.getName());
        }
        if (memberProfileUpdateRequest.getPhone() != null) {
            memberEntity.setPhone(memberProfileUpdateRequest.getPhone());
        }
        if (memberProfileUpdateRequest.getPhotoUrl() != null) {
            memberEntity.setPhotoUrl(memberProfileUpdateRequest.getPhotoUrl());
        }
        if (memberProfileUpdateRequest.getWorkTypeId() != null) {
            memberEntity.setWorkTypeId(memberProfileUpdateRequest.getWorkTypeId());
        }
        if (memberProfileUpdateRequest.getSleepTime() != null) {
            memberEntity.setSleepTime(memberProfileUpdateRequest.getSleepTime());
        }
        if (memberProfileUpdateRequest.getSmoking() != null) {
            memberEntity.setSmoking(memberProfileUpdateRequest.getSmoking());
        }
        if (memberProfileUpdateRequest.getDrinking() != null) {
            memberEntity.setDrinking(memberProfileUpdateRequest.getDrinking());
        }
        if (memberProfileUpdateRequest.getMbti() != null) {
            memberEntity.setMbti(memberProfileUpdateRequest.getMbti());
        }

        memberRepository.updateMember(memberEntity);

        // 2) N:N 관계 테이블 갱신
        //    가장 단순하고 안전한 패턴: delete all → insert all
        if (memberProfileUpdateRequest.getHobbyIds() != null) {
            memberHobbyRepository.deleteByMemberId(memberId);
            if (!memberProfileUpdateRequest.getHobbyIds().isEmpty()) {
                memberHobbyRepository.insertMemberHobbies(memberId, memberProfileUpdateRequest.getHobbyIds());
            }
        }

        if (memberProfileUpdateRequest.getPreferenceIds() != null) {
            memberPreferenceRepository.deleteByMemberId(memberId);
            if (!memberProfileUpdateRequest.getPreferenceIds().isEmpty()) {
                memberPreferenceRepository.insertMemberPreferences(memberId, memberProfileUpdateRequest.getPreferenceIds());
            }
        }

        if (memberProfileUpdateRequest.getPetIds() != null) {
            memberPetRepository.deleteByMemberId(memberId);
            if (!memberProfileUpdateRequest.getPetIds().isEmpty()) {
                memberPetRepository.insertMemberPets(memberId, memberProfileUpdateRequest.getPetIds());
            }
        }

        // 3) 응답용 데이터 재조회
        WorkTypeEntity workTypeEntity = null;
        if (memberEntity.getWorkTypeId() != null) {
            workTypeEntity = workTypeRepository.findById(memberEntity.getWorkTypeId())
                    .orElse(null);
        }

        List<HobbyEntity> hobbyEntities = hobbyRepository.findByMemberId(memberId);
        List<PreferenceEntity> preferenceEntities = preferenceRepository.findByMemberId(memberId);
        List<PetEntity> petEntities = petRepository.findByMemberId(memberId);

        return toMemberResponse(memberEntity, workTypeEntity, hobbyEntities, preferenceEntities, petEntities);
    }

    /**
     * 회원 탈퇴 처리
     *
     * 왜 이렇게 썼는지:
     * - 실제 members row를 삭제하지 않고 deleted 플래그로 관리(soft delete)해서
     *   FK 제약, 채팅/방 이력, 통계 데이터를 보존하기 위함.
     * - 연관 테이블은 도메인 정책에 맞게 정리하고,
     *   채팅 메시지는 남기되 채팅방은 "탈퇴한 회원 기준으로만" 나가기 처리한다.
     */
    @Override
    @Transactional
    public void deleteMember(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        if (memberEntity.getDeleted() == 1) {
            return;
        }
        // 0) Refresh Token 삭제 (로그아웃 효과)
        // - 탈퇴 후 더 이상 토큰 재발급/인증이 되지 않도록
        //   해당 회원의 refresh token 레코드를 먼저 제거한다.
        tokenRefreshRepository.deleteByMemberId(memberId);

        // 1) N:N 관계(member_* 조인 테이블) 관계 제거
        // - 회원과 취미/선호/반려동물 관계를 모두 끊어서
        //   추후 조회 시 더 이상 이 회원의 취향 정보가 보이지 않도록 한다.
        memberPetRepository.deleteByMemberId(memberId);
        memberPreferenceRepository.deleteByMemberId(memberId);
        memberHobbyRepository.deleteByMemberId(memberId);

        // 2) 찜(favorites) 삭제
        // - 내가 찜해둔 방 목록은 탈퇴 후 의미가 없으므로 전부 삭제.
        favoriteRepository.deleteByMemberId(memberId);

        // 3) 알림(notifications) 삭제
        // - 나에게 온 알림들도 더 이상 사용할 수 없으므로 한 번에 삭제.
        notificationRepository.deleteByMemberId(memberId);

        // 4) 채팅방 정리
        // - 채팅 메시지(chat_messages)는 기록 보존을 위해 그대로 두고,
        //   채팅방(chat_rooms)은 "탈퇴한 회원 기준으로만" 나가기 처리한다.
        // - 상대방 입장에서는 기존 대화방과 메시지 이력이 그대로 남는다.
        chatRoomRepository.markDeletedByMember(memberId);

        // 5) 내가 올린 방(rooms) 정리 - 논리 삭제
        // - 방 이력을 완전히 지우기보다는,
        //   status = CLOSED, deleted = 1 로 더 이상 노출되지 않게 처리한다.
        roomRepository.closeAndDeleteByMemberId(memberId);

        // 6) 최종적으로 members.deleted = 1 로 soft delete
        memberRepository.softDeleteMember(memberId);
    }
}
