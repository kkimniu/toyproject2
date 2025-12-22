package com.roommate.domain.room.controller;

import com.roommate.domain.room.dto.response.RoomTypeResponse;
import com.roommate.domain.room.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeRestController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    public List<RoomTypeResponse> getRoomTypes() {
        return roomTypeService.getRoomTypes();
    }
}
