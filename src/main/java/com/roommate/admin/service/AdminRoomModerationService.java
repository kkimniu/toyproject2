package com.roommate.admin.service;

import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminRoomModerationRequest;

public interface AdminRoomModerationService {
    AdminReportListItemResponse deleteReportedRoom(Long reportId, AdminRoomModerationRequest request, Long adminId);
}
