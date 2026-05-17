package com.roommate.admin.controller;

import com.roommate.admin.dto.AdminDashboardSummaryResponse;
import com.roommate.admin.dto.AdminMemberListItemResponse;
import com.roommate.admin.dto.AdminMemberListResponse;
import com.roommate.admin.dto.AdminMemberStatusUpdateRequest;
import com.roommate.admin.dto.AdminReportListResponse;
import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminReportStatusUpdateRequest;
import com.roommate.admin.service.AdminDashboardService;
import com.roommate.admin.service.AdminMemberService;
import com.roommate.admin.service.AdminReportService;
import com.roommate.common.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final AdminMemberService adminMemberService;
    private final AdminReportService adminReportService;
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard/summary")
    public AdminDashboardSummaryResponse getDashboardSummary() {
        return adminDashboardService.getSummary();
    }

    @GetMapping("/members")
    public AdminMemberListResponse getMembers(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return adminMemberService.getMembers(page, size);
    }

    @PatchMapping("/members/{memberId}/status")
    public AdminMemberListItemResponse updateMemberStatus(@PathVariable Long memberId,
                                                          @RequestBody AdminMemberStatusUpdateRequest request,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminMemberService.updateMemberStatus(memberId, request.getStatus(), userDetails.getMemberId());
    }

    @GetMapping("/reports")
    public AdminReportListResponse getReports(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return adminReportService.getReports(page, size);
    }

    @PatchMapping("/reports/{reportId}/status")
    public AdminReportListItemResponse updateReportStatus(@PathVariable Long reportId,
                                                          @RequestBody AdminReportStatusUpdateRequest request,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminReportService.updateReportStatus(
                reportId,
                request.getStatus(),
                request.getResolutionType(),
                request.getResolutionMessage(),
                userDetails.getMemberId()
        );
    }
}
