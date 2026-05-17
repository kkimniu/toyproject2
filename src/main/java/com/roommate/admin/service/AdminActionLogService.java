package com.roommate.admin.service;

import com.roommate.admin.dto.AdminActionLogListResponse;

public interface AdminActionLogService {
    void logMemberStatusChange(Long adminId, Long memberId, String actionType);

    void logReportResolved(Long adminId, Long reportId, String resolutionType);

    AdminActionLogListResponse getLogs(int page, int size);
}
