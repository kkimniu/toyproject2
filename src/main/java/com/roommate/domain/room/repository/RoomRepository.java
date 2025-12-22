package com.roommate.domain.room.repository;

import com.roommate.domain.room.dto.response.MyRoomListItemResponse;
import com.roommate.domain.room.entity.RoomDetailEntity;
import com.roommate.domain.room.entity.RoomEntity;
import com.roommate.domain.room.entity.RoomMapItemEntity;
import com.roommate.domain.room.entity.RoomStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomRepository {

    void closeAndDeleteByMemberId(@Param("memberId") Long memberId);

    void insertRoom(RoomEntity room);

    void updateRoom(RoomEntity room);

    void softDeleteRoom(@Param("roomId") Long roomId, @Param("memberId") Long memberId);

    void updateRoomStatus(@Param("roomId") Long roomId, @Param("memberId") Long memberId, @Param("status") RoomStatusEnum status);

    RoomEntity findById(@Param("roomId") Long roomId);

    // 지도용 목록 조회
    List<RoomMapItemEntity> findForMap(@Param("north") double north, @Param("south") double south, @Param("east") double east, @Param("west") double west, @Param("zoom") int zoom);

    // 상세 조회용 (조인된 결과를 response DTO로 바로 받을 수도 있음)
    RoomDetailEntity findDetailById(@Param("roomId") Long roomId);

    //방 등록 조회
    List<MyRoomListItemResponse> findMyRooms(@Param("memberId") Long memberId);
}
