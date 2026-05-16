package com.roommate.admin.service;

import com.roommate.admin.dto.AdminReportListResponse;

public interface AdminReportService {
    AdminReportListResponse getReports(int page, int size);
}
