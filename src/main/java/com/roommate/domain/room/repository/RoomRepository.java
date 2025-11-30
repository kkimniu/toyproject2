package com.roommate.domain.room.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoomRepository {
    void closeAndDeleteByMemberId(@Param("memberId") Long memberId);
}
