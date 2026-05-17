package com.roommate.admin.service;

import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminReportListResponse;
import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final ReportRepository reportRepository;

    @Override
    public AdminReportListResponse getReports(int page,
                                              int size,
                                              String status,
                                              String reporter,
                                              String target,
                                              String from,
                                              String to) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;

        String safeStatus = normalize(status);
        String safeReporter = normalize(reporter);
        String safeTarget = normalize(target);
        String safeFrom = normalize(from);
        String safeTo = normalize(to);

        long totalCount = reportRepository.countReportsForAdmin(safeStatus, safeReporter, safeTarget, safeFrom, safeTo);
        List<AdminReportListItemResponse> items = reportRepository.findReportsForAdmin(
                safeStatus,
                safeReporter,
                safeTarget,
                safeFrom,
                safeTo,
                safeSize,
                offset
        );
        int totalPages = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / safeSize);
        boolean hasNext = safePage < totalPages;

        return new AdminReportListResponse(items, safePage, safeSize, totalCount, totalPages, hasNext);
    }

    @Override
    public AdminReportListItemResponse updateReportStatus(Long reportId,
                                                          String status,
                                                          String resolutionType,
                                                          String resolutionMessage,
                                                          Long processedBy) {
        if (!"RESOLVED".equals(status)) {
            throw new ApiException(ErrorCode.ADMIN_REPORT_STATUS_INVALID);
        }
        if (!isValidResolutionType(resolutionType)) {
            throw new ApiException(ErrorCode.ADMIN_REPORT_RESOLUTION_TYPE_INVALID);
        }
        if (resolutionMessage == null || resolutionMessage.isBlank()) {
            throw new ApiException(ErrorCode.ADMIN_REPORT_RESOLUTION_MESSAGE_REQUIRED);
        }

        AdminReportListItemResponse report = reportRepository.findReportForAdminById(reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_REPORT_NOT_FOUND));

        if ("RESOLVED".equals(report.getStatus())) {
            throw new ApiException(ErrorCode.ADMIN_REPORT_ALREADY_RESOLVED);
        }

        int updatedCount = reportRepository.updateReportStatusForAdmin(
                reportId,
                status,
                resolutionType,
                resolutionMessage,
                processedBy
        );
        if (updatedCount != 1) {
            throw new ApiException(ErrorCode.ADMIN_REPORT_RESOLVE_FAILED);
        }

        return reportRepository.findReportForAdminById(reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_REPORT_NOT_FOUND));
    }

    private boolean isValidResolutionType(String resolutionType) {
        return "ACCEPTED".equals(resolutionType)
                || "REJECTED".equals(resolutionType)
                || "NO_ACTION".equals(resolutionType);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
