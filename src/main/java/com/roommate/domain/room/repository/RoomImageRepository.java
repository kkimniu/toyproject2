package com.roommate.domain.room.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomImageRepository {
    /**
     *  특정 방의 기존 이미지 전체 삭제
     * 왜: 수정 시 기존 이미지를 전부 지우고 새 이미지로 교체하는 정책
     */
    void deleteByRoomId(@Param("roomId") Long roomId);

    /**
     *  단건 이미지 저장
     * 왜: 여러 장 저장 시 반복 호출용
     */
    void insertRoomImage(@Param("roomId") Long roomId, @Param("imageUrl") String imageUrl, @Param("sortOrder") Integer sortOrder);

    /**
     *  방의 이미지 URL 목록 조회 (상세 조회용)
     */
    List<String> findImageUrlsByRoomId(@Param("roomId") Long roomId);

}
