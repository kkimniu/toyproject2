package com.roommate.admin.service;

import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminReportListResponse;

public interface AdminReportService {
    AdminReportListResponse getReports(int page,
                                       int size,
                                       String status,
                                       String reportType,
                                       String reporter,
                                       String target,
                                       String from,
                                       String to);

    AdminReportListItemResponse updateReportStatus(Long reportId,
                                                   String status,
                                                   String resolutionType,
                                                   String resolutionMessage,
                                                   Long processedBy);
}
