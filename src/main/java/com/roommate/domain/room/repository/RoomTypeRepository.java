package com.roommate.domain.room.repository;

import com.roommate.domain.room.entity.RoomTypeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoomTypeRepository {
    List<RoomTypeEntity> findAll();

    RoomTypeEntity findById(Long roomTypeId);

    void insert(RoomTypeEntity entity);

    void update(RoomTypeEntity entity);

    void delete(Long roomTypeId);
}
