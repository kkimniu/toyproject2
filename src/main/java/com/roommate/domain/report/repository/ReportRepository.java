package com.roommate.domain.report.repository;

import com.roommate.admin.dto.AdminReportListItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportRepository {
    long countReportsForAdmin();

    List<AdminReportListItemResponse> findReportsForAdmin(@Param("limit") int limit, @Param("offset") int offset);
}
