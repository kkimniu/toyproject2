package com.roommate.admin.service;

import com.roommate.admin.dto.AdminActionLogItemResponse;
import com.roommate.admin.dto.AdminActionLogListResponse;
import com.roommate.domain.admin.repository.AdminActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminActionLogServiceImpl implements AdminActionLogService {

    private final AdminActionLogRepository adminActionLogRepository;

    @Override
    public void logMemberStatusChange(Long adminId, Long memberId, String actionType) {
        adminActionLogRepository.save(adminId, actionType, "MEMBER", memberId, null);
    }

    @Override
    public void logReportResolved(Long adminId, Long reportId, String resolutionType) {
        adminActionLogRepository.save(adminId, "REPORT_RESOLVED", "REPORT", reportId, resolutionType);
    }

    @Override
    public void logMemberRoleChange(Long adminId, Long memberId, String actionType) {
        adminActionLogRepository.save(adminId, actionType, "MEMBER", memberId, null);
    }

    @Override
    public AdminActionLogListResponse getLogs(int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;

        long totalCount = adminActionLogRepository.countLogsForAdmin();
        List<AdminActionLogItemResponse> items = adminActionLogRepository.findLogsForAdmin(safeSize, offset);
        int totalPages = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / safeSize);
        boolean hasNext = safePage < totalPages;

        return new AdminActionLogListResponse(items, safePage, safeSize, totalCount, totalPages, hasNext);
    }
}
