package com.roommate.domain.room.service;

import com.roommate.domain.room.dto.response.RoomTypeResponse;

import java.util.List;

public interface RoomTypeService {

    public List<RoomTypeResponse> getRoomTypes();

}
