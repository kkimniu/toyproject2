package com.roommate.domain.report.repository;

import com.roommate.admin.dto.AdminReportListItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReportRepository {
    long countReportsForAdmin();

    List<AdminReportListItemResponse> findReportsForAdmin(@Param("limit") int limit, @Param("offset") int offset);

    Optional<AdminReportListItemResponse> findReportForAdminById(@Param("reportId") Long reportId);

    int updateReportStatusForAdmin(@Param("reportId") Long reportId,
                                   @Param("status") String status,
                                   @Param("resolutionType") String resolutionType,
                                   @Param("resolutionMessage") String resolutionMessage,
                                   @Param("processedBy") Long processedBy);
}
