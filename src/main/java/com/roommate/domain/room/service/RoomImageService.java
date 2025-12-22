package com.roommate.domain.room.service;

import java.util.List;

public interface RoomImageService {
    /**
     * 방 등록 시: temp 파일들을 "사용 처리"하고(room 소유자 검증 포함),
     * room_image 테이블에 순서대로 저장한다.
     */
    void attachTempImagesToRoom(Long roomId, Long memberId, List<Long> tempFileIds);

    /**
     * 방 수정 시: 기존 이미지를 전체 교체한다.
     * (지금 정책 그대로 유지)
     */
    void replaceRoomImages(Long roomId, List<String> imageUrls);

    List<String> findImageUrlsByRoomId(Long roomId);
}
