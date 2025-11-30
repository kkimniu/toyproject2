package com.roommate.domain.notification.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationRepository {
    void deleteByMemberId(@Param("memberId") Long memberId);
}
