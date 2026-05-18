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

    long countLogsForAdmin(@Param("actionType") String actionType,
                           @Param("admin") String admin,
                           @Param("from") String from,
                           @Param("to") String to);

    List<AdminActionLogItemResponse> findLogsForAdmin(@Param("actionType") String actionType,
                                                      @Param("admin") String admin,
                                                      @Param("from") String from,
                                                      @Param("to") String to,
                                                      @Param("limit") int limit,
                                                      @Param("offset") int offset);
}
