package com.roommate.admin.service;

import com.roommate.admin.dto.AdminDashboardSummaryResponse;
import com.roommate.admin.dto.AdminDashboardSettingsRequest;
import com.roommate.admin.dto.AdminDashboardSettingsResponse;

public interface AdminDashboardService {
    AdminDashboardSummaryResponse getSummary(Integer trendDays);

    AdminDashboardSettingsResponse getSettings();

    AdminDashboardSettingsResponse updateSettings(AdminDashboardSettingsRequest request);
}
