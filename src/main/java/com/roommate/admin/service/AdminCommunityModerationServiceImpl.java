package com.roommate.admin.service;

import com.roommate.admin.dto.AdminCommunityModerationRequest;
import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.community.repository.CommunityCommentRepository;
import com.roommate.domain.community.repository.CommunityPostRepository;
import com.roommate.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCommunityModerationServiceImpl implements AdminCommunityModerationService {
    private final ReportRepository reportRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final AdminReportService adminReportService;

    @Override
    @Transactional
    public AdminReportListItemResponse moderateReport(Long reportId, AdminCommunityModerationRequest request, Long adminId) {
        AdminReportListItemResponse report = reportRepository.findReportForAdminById(reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_REPORT_NOT_FOUND));
        String action = normalizeAction(request == null ? null : request.getAction());
        String message = normalizeMessage(request == null ? null : request.getResolutionMessage(), action);

        int updated = switch (String.valueOf(report.getReportType()).toUpperCase()) {
            case "COMMUNITY_POST" -> moderatePost(report.getCommunityPostId(), action);
            case "COMMUNITY_COMMENT" -> moderateComment(report.getCommunityCommentId(), action);
            default -> throw new ApiException(ErrorCode.INVALID_REQUEST);
        };
        if (updated != 1) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return adminReportService.updateReportStatus(reportId, "RESOLVED", "ACCEPTED", message, adminId);
    }

    private int moderatePost(Long postId, String action) {
        if (postId == null) throw new ApiException(ErrorCode.INVALID_REQUEST);
        if ("BLIND".equals(action)) return communityPostRepository.adminBlind(postId);
        return communityPostRepository.adminDelete(postId);
    }

    private int moderateComment(Long commentId, String action) {
        if (commentId == null) throw new ApiException(ErrorCode.INVALID_REQUEST);
        if ("BLIND".equals(action)) return communityCommentRepository.adminBlind(commentId);
        return communityCommentRepository.adminDelete(commentId);
    }

    private String normalizeAction(String value) {
        if (value == null || value.isBlank()) throw new ApiException(ErrorCode.INVALID_REQUEST);
        String upper = value.trim().toUpperCase();
        if (!"BLIND".equals(upper) && !"DELETE".equals(upper)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        return upper;
    }

    private String normalizeMessage(String value, String action) {
        if (value != null && !value.isBlank()) return value.trim();
        return "BLIND".equals(action)
                ? "커뮤니티 콘텐츠가 운영 정책에 따라 블라인드 처리되었습니다."
                : "커뮤니티 콘텐츠가 운영 정책에 따라 삭제 처리되었습니다.";
    }
}
