package com.roommate.domain.report.service;

import com.roommate.domain.report.dto.MyReportListItemResponse;

import java.util.List;

public interface ReportService {
    List<MyReportListItemResponse> getMyReports(Long reporterId);
}
