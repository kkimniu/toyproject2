package com.roommate.domain.admin.repository;

import com.roommate.admin.dto.AdminActionLogItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminActionLogRepository {
    void save(@Param("adminId") Long adminId,
              @Param("actionType") String actionType,
              @Param("targetType") String targetType,
              @Param("targetId") Long targetId,
              @Param("actionDetail") String actionDetail);

    long countLogsForAdmin();

    List<AdminActionLogItemResponse> findLogsForAdmin(@Param("limit") int limit, @Param("offset") int offset);
}
