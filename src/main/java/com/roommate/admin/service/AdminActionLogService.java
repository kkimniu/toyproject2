package com.roommate.admin.service;

import com.roommate.admin.dto.AdminActionLogListResponse;

public interface AdminActionLogService {
    void logMemberStatusChange(Long adminId, Long memberId, String actionType);

    void logReportResolved(Long adminId, Long reportId, String resolutionType);

    void logMemberRoleChange(Long adminId, Long memberId, String actionType);

    void logMemberDeleted(Long adminId, Long memberId);

    AdminActionLogListResponse getLogs(int page, int size, String actionType, String admin, String from, String to);
}
