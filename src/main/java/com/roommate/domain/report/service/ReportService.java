package com.roommate.domain.report.service;

import com.roommate.domain.report.dto.MyReportListItemResponse;
import com.roommate.domain.report.dto.ReportRequest;
import com.roommate.domain.report.dto.ReportResponse;

import java.util.List;

public interface ReportService {
    ReportResponse createMemberReport(Long reporterId, Long targetMemberId, ReportRequest request);

    ReportResponse createRoomReport(Long reporterId, Long roomId, ReportRequest request);

    ReportResponse createChatReport(Long reporterId, Long chatRoomId, ReportRequest request);

    ReportResponse createCommunityPostReport(Long reporterId, Long communityPostId, ReportRequest request);

    ReportResponse createCommunityCommentReport(Long reporterId, Long communityCommentId, ReportRequest request);

    List<MyReportListItemResponse> getMyReports(Long reporterId);

    MyReportListItemResponse getMyReport(Long reporterId, Long reportId);
}
