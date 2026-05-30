package com.roommate.admin.service;

import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminRoomModerationRequest;
import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.report.repository.ReportRepository;
import com.roommate.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminRoomModerationServiceImpl implements AdminRoomModerationService {
    private final ReportRepository reportRepository;
    private final RoomRepository roomRepository;
    private final AdminReportService adminReportService;

    @Override
    @Transactional
    public AdminReportListItemResponse deleteReportedRoom(Long reportId, AdminRoomModerationRequest request, Long adminId) {
        AdminReportListItemResponse report = reportRepository.findReportForAdminById(reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_REPORT_NOT_FOUND));

        if (!"ROOM".equalsIgnoreCase(String.valueOf(report.getReportType())) || report.getRoomId() == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        int updated = roomRepository.adminDeleteRoom(report.getRoomId());
        if (updated != 1) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        String message = normalizeMessage(request == null ? null : request.getResolutionMessage());
        return adminReportService.updateReportStatus(reportId, "RESOLVED", "ACCEPTED", message, adminId);
    }

    private String normalizeMessage(String value) {
        if (value != null && !value.isBlank()) return value.trim();
        return "신고된 방을 운영 정책에 따라 삭제 처리했습니다.";
    }
}
