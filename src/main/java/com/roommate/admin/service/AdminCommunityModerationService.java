package com.roommate.admin.service;

import com.roommate.admin.dto.AdminCommunityModerationRequest;
import com.roommate.admin.dto.AdminReportListItemResponse;

public interface AdminCommunityModerationService {
    AdminReportListItemResponse moderateReport(Long reportId, AdminCommunityModerationRequest request, Long adminId);
}
