package com.roommate.admin.controller;

import com.roommate.admin.dto.AdminActionLogListResponse;
import com.roommate.admin.dto.AdminDashboardSummaryResponse;
import com.roommate.admin.dto.AdminMemberListItemResponse;
import com.roommate.admin.dto.AdminMemberListResponse;
import com.roommate.admin.dto.AdminMemberStatusUpdateRequest;
import com.roommate.admin.dto.AdminMemberRoleUpdateRequest;
import com.roommate.admin.dto.AdminReportListResponse;
import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminReportStatusUpdateRequest;
import com.roommate.admin.service.AdminActionLogService;
import com.roommate.admin.service.AdminDashboardService;
import com.roommate.admin.service.AdminMemberService;
import com.roommate.admin.service.AdminReportService;
import com.roommate.common.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final AdminActionLogService adminActionLogService;
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard/summary")
    public AdminDashboardSummaryResponse getDashboardSummary() {
        return adminDashboardService.getSummary();
    }

    @GetMapping("/members")
    public AdminMemberListResponse getMembers(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String role,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(required = false) String from,
                                              @RequestParam(required = false) String to) {
        return adminMemberService.getMembers(page, size, keyword, role, status, from, to);
    }

    @GetMapping("/action-logs")
    public AdminActionLogListResponse getActionLogs(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "20") int size,
                                                    @RequestParam(name = "action_type", required = false) String actionType,
                                                    @RequestParam(required = false) String admin,
                                                    @RequestParam(required = false) String from,
                                                    @RequestParam(required = false) String to) {
        return adminActionLogService.getLogs(page, size, actionType, admin, from, to);
    }

    @PatchMapping("/members/{memberId}/status")
    public AdminMemberListItemResponse updateMemberStatus(@PathVariable Long memberId,
                                                          @RequestBody AdminMemberStatusUpdateRequest request,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminMemberService.updateMemberStatus(
                memberId,
                request.getStatus(),
                userDetails.getMemberId(),
                userDetails.getRole()
        );
    }

    @PatchMapping("/members/{memberId}/role")
    public AdminMemberListItemResponse updateMemberRole(@PathVariable Long memberId,
                                                        @RequestBody AdminMemberRoleUpdateRequest request,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminMemberService.updateMemberRole(
                memberId,
                request.getRole(),
                userDetails.getMemberId(),
                userDetails.getRole()
        );
    }

    @DeleteMapping("/members/{memberId}")
    public void deleteMember(@PathVariable Long memberId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        adminMemberService.deleteMember(
                memberId,
                userDetails.getMemberId(),
                userDetails.getRole()
        );
    }

    @GetMapping("/reports")
    public AdminReportListResponse getReports(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(name = "report_type", required = false) String reportType,
                                              @RequestParam(required = false) String reporter,
                                              @RequestParam(required = false) String target,
                                              @RequestParam(required = false) String from,
                                              @RequestParam(required = false) String to) {
        return adminReportService.getReports(page, size, status, reportType, reporter, target, from, to);
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
