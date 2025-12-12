package com.roommate.domain.room.service;

import com.roommate.domain.room.dto.request.RoomCreateRequest;
import com.roommate.domain.room.dto.request.RoomStatusUpdateRequest;
import com.roommate.domain.room.dto.request.RoomUpdateRequest;
import com.roommate.domain.room.dto.response.RoomDetailResponse;
import com.roommate.domain.room.dto.response.RoomMapItemResponse;

import java.util.List;

public interface RoomService {

    public Long createRoom(RoomCreateRequest roomCreateRequest, Long memberId);

    public void updateRoom(Long roomId, RoomUpdateRequest roomUpdateRequest, Long memberId);

    public void deleteRoom(Long roomId, Long memberId);

    public void changeStatus(Long roomId, RoomStatusUpdateRequest roomStatusUpdateRequest, Long memberId);

    public RoomDetailResponse getRoomDetail(Long roomId, Long currentMemberId);

    public List<RoomMapItemResponse> getRoomsForMap(double north, double south, double east, double west, int zoom);

}
