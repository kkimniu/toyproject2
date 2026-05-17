package com.roommate.domain.report.repository;

import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.domain.report.dto.MyReportListItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReportRepository {
    long countReportsForAdmin(@Param("status") String status,
                              @Param("reporter") String reporter,
                              @Param("target") String target,
                              @Param("from") String from,
                              @Param("to") String to);

    long countReportsByStatusForAdmin(@Param("status") String status);

    List<AdminReportListItemResponse> findReportsForAdmin(@Param("status") String status,
                                                          @Param("reporter") String reporter,
                                                          @Param("target") String target,
                                                          @Param("from") String from,
                                                          @Param("to") String to,
                                                          @Param("limit") int limit,
                                                          @Param("offset") int offset);

    Optional<AdminReportListItemResponse> findReportForAdminById(@Param("reportId") Long reportId);

    int updateReportStatusForAdmin(@Param("reportId") Long reportId,
                                   @Param("status") String status,
                                   @Param("resolutionType") String resolutionType,
                                   @Param("resolutionMessage") String resolutionMessage,
                                   @Param("processedBy") Long processedBy);

    List<MyReportListItemResponse> findMyReports(@Param("reporterId") Long reporterId);
}
