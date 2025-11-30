package com.roommate.domain.chat.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatRoomRepository {
    void markDeletedByMember(@Param("memberId") Long memberId);
}
