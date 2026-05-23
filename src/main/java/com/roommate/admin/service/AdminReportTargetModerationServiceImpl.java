package com.roommate.admin.service;

import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminReportTargetModerationRequest;
import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.chat.entity.ChatRoomEntity;
import com.roommate.domain.chat.repository.ChatRoomRepository;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReportTargetModerationServiceImpl implements AdminReportTargetModerationService {
    private final ReportRepository reportRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AdminMemberService adminMemberService;
    private final AdminReportService adminReportService;

    @Override
    @Transactional
    public AdminReportListItemResponse deleteReportedMember(
            Long reportId,
            AdminReportTargetModerationRequest request,
            Long adminId,
            MemberRoleEnum adminRole
    ) {
        AdminReportListItemResponse report = findReport(reportId);
        if (!"MEMBER".equalsIgnoreCase(String.valueOf(report.getReportType())) || report.getTargetMemberId() == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        adminMemberService.deleteMember(report.getTargetMemberId(), adminId, adminRole);
        return adminReportService.updateReportStatus(
                reportId,
                "RESOLVED",
                "ACCEPTED",
                normalizeMessage(request, "신고된 회원을 운영 정책에 따라 탈퇴 처리했습니다."),
                adminId
        );
    }

    @Override
    @Transactional
    public AdminReportListItemResponse deleteReportedChatRoom(
            Long reportId,
            AdminReportTargetModerationRequest request,
            Long adminId
    ) {
        AdminReportListItemResponse report = findReport(reportId);
        if (!"CHAT".equalsIgnoreCase(String.valueOf(report.getReportType())) || report.getChatRoomId() == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        ChatRoomEntity chatRoom = chatRoomRepository.findById(report.getChatRoomId());
        if (chatRoom == null) {
            throw new ApiException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        int updated = chatRoomRepository.adminMarkDeleted(report.getChatRoomId());
        chatRoomRepository.adminHideMembers(report.getChatRoomId());
        if (updated != 1) {
            throw new ApiException(ErrorCode.CHAT_ROOM_DELETE_FAILED);
        }

        return adminReportService.updateReportStatus(
                reportId,
                "RESOLVED",
                "ACCEPTED",
                normalizeMessage(request, "신고된 채팅방을 운영 정책에 따라 삭제 처리했습니다."),
                adminId
        );
    }

    private AdminReportListItemResponse findReport(Long reportId) {
        return reportRepository.findReportForAdminById(reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_REPORT_NOT_FOUND));
    }

    private String normalizeMessage(AdminReportTargetModerationRequest request, String defaultMessage) {
        String value = request == null ? null : request.getResolutionMessage();
        return value == null || value.isBlank() ? defaultMessage : value.trim();
    }
}
