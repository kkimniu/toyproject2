package com.roommate.domain.room.service;

import com.roommate.domain.room.dto.response.RoomTypeResponse;
import com.roommate.domain.room.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomTypeServiceImpl implements RoomTypeService{
    private final RoomTypeRepository roomTypeRepository;

    @Override
    public List<RoomTypeResponse> getRoomTypes() {
        return roomTypeRepository.findAll().stream().map(rt -> new RoomTypeResponse(rt.getRoomTypeId(),rt.getRoomTypeName())).toList();
    }
}
