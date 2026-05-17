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
    public AdminReportListResponse getReports(int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;

        long totalCount = reportRepository.countReportsForAdmin();
        List<AdminReportListItemResponse> items = reportRepository.findReportsForAdmin(safeSize, offset);
        int totalPages = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / safeSize);
        boolean hasNext = safePage < totalPages;

        return new AdminReportListResponse(items, safePage, safeSize, totalCount, totalPages, hasNext);
    }

    @Override
    public AdminReportListItemResponse updateReportStatus(Long reportId, String status) {
        if (!"RESOLVED".equals(status)) {
            throw new ApiException(ErrorCode.ADMIN_REPORT_STATUS_INVALID);
        }

        AdminReportListItemResponse report = reportRepository.findReportForAdminById(reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_REPORT_NOT_FOUND));

        if ("RESOLVED".equals(report.getStatus())) {
            throw new ApiException(ErrorCode.ADMIN_REPORT_ALREADY_RESOLVED);
        }

        int updatedCount = reportRepository.updateReportStatusForAdmin(reportId, status);
        if (updatedCount != 1) {
            throw new ApiException(ErrorCode.ADMIN_REPORT_RESOLVE_FAILED);
        }

        return reportRepository.findReportForAdminById(reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_REPORT_NOT_FOUND));
    }
}
