package com.roommate.domain.report.repository;

import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminReportTrendResponse;
import com.roommate.admin.dto.AdminSanctionCandidateResponse;
import com.roommate.domain.report.dto.MyReportListItemResponse;
import com.roommate.domain.report.entity.ReportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReportRepository {
    int saveMemberReport(ReportEntity report);

    int saveRoomReport(ReportEntity report);

    int saveChatReport(ReportEntity report);

    int saveCommunityPostReport(ReportEntity report);

    int saveCommunityCommentReport(ReportEntity report);

    boolean existsMemberReport(@Param("reporterId") Long reporterId,
                               @Param("targetMemberId") Long targetMemberId);

    boolean existsRoomReport(@Param("reporterId") Long reporterId,
                             @Param("roomId") Long roomId);

    boolean existsChatReport(@Param("reporterId") Long reporterId,
                             @Param("chatRoomId") Long chatRoomId);

    boolean existsCommunityPostReport(@Param("reporterId") Long reporterId,
                                      @Param("communityPostId") Long communityPostId);

    boolean existsCommunityCommentReport(@Param("reporterId") Long reporterId,
                                         @Param("communityCommentId") Long communityCommentId);

    long countReportsForAdmin(@Param("status") String status,
                              @Param("reportType") String reportType,
                              @Param("reporter") String reporter,
                              @Param("target") String target,
                              @Param("from") String from,
                              @Param("to") String to);

    long countReportsByStatusForAdmin(@Param("status") String status);

    long countSanctionCandidatesForAdmin(@Param("threshold") int threshold);

    List<AdminSanctionCandidateResponse> findSanctionCandidatesForAdmin(@Param("threshold") int threshold,
                                                                        @Param("limit") int limit);

    List<AdminReportTrendResponse> findReportTrendsForAdmin(@Param("days") int days);

    List<AdminReportListItemResponse> findReportsForAdmin(@Param("status") String status,
                                                          @Param("reportType") String reportType,
                                                          @Param("reporter") String reporter,
                                                          @Param("target") String target,
                                                          @Param("from") String from,
                                                          @Param("to") String to,
                                                          @Param("limit") int limit,
                                                          @Param("offset") int offset);

    Optional<AdminReportListItemResponse> findReportForAdminById(@Param("reportId") Long reportId);

    Optional<MyReportListItemResponse> findMyReportById(@Param("reporterId") Long reporterId,
                                                        @Param("reportId") Long reportId);

    int updateReportStatusForAdmin(@Param("reportId") Long reportId,
                                   @Param("status") String status,
                                   @Param("resolutionType") String resolutionType,
                                   @Param("resolutionMessage") String resolutionMessage,
                                   @Param("processedBy") Long processedBy);

    List<MyReportListItemResponse> findMyReports(@Param("reporterId") Long reporterId);
}
