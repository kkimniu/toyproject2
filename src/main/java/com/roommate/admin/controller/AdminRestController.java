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
import com.roommate.admin.dto.AdminReportTargetModerationRequest;
import com.roommate.admin.dto.AdminRoomModerationRequest;
import com.roommate.admin.dto.AdminCategoryRequest;
import com.roommate.admin.dto.AdminCategoryResponse;
import com.roommate.admin.dto.AdminCommunityModerationRequest;
import com.roommate.admin.service.AdminActionLogService;
import com.roommate.admin.service.AdminCategoryService;
import com.roommate.admin.service.AdminCommunityModerationService;
import com.roommate.admin.service.AdminDashboardService;
import com.roommate.admin.service.AdminMemberService;
import com.roommate.admin.service.AdminReportService;
import com.roommate.admin.service.AdminReportTargetModerationService;
import com.roommate.admin.service.AdminRoomModerationService;
import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.notice.dto.NoticeListResponse;
import com.roommate.domain.notice.dto.NoticeRequest;
import com.roommate.domain.notice.dto.NoticeResponse;
import com.roommate.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final AdminMemberService adminMemberService;
    private final AdminReportService adminReportService;
    private final AdminActionLogService adminActionLogService;
    private final AdminDashboardService adminDashboardService;
    private final NoticeService noticeService;
    private final AdminCategoryService adminCategoryService;
    private final AdminCommunityModerationService adminCommunityModerationService;
    private final AdminRoomModerationService adminRoomModerationService;
    private final AdminReportTargetModerationService adminReportTargetModerationService;

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

    @PatchMapping("/reports/{reportId}/community-moderation")
    public AdminReportListItemResponse moderateCommunityReport(@PathVariable Long reportId,
                                                               @RequestBody AdminCommunityModerationRequest request,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminCommunityModerationService.moderateReport(reportId, request, userDetails.getMemberId());
    }

    @PatchMapping("/reports/{reportId}/room-delete")
    public AdminReportListItemResponse deleteReportedRoom(@PathVariable Long reportId,
                                                          @RequestBody AdminRoomModerationRequest request,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminRoomModerationService.deleteReportedRoom(reportId, request, userDetails.getMemberId());
    }

    @PatchMapping("/reports/{reportId}/member-delete")
    public AdminReportListItemResponse deleteReportedMember(@PathVariable Long reportId,
                                                            @RequestBody AdminReportTargetModerationRequest request,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminReportTargetModerationService.deleteReportedMember(
                reportId,
                request,
                userDetails.getMemberId(),
                userDetails.getRole()
        );
    }

    @PatchMapping("/reports/{reportId}/chat-delete")
    public AdminReportListItemResponse deleteReportedChatRoom(@PathVariable Long reportId,
                                                              @RequestBody AdminReportTargetModerationRequest request,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminReportTargetModerationService.deleteReportedChatRoom(reportId, request, userDetails.getMemberId());
    }

    @GetMapping("/notices")
    public NoticeListResponse getNotices(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Boolean published) {
        return noticeService.getAdminNotices(page, size, keyword, published);
    }

    @GetMapping("/notices/{noticeId}")
    public NoticeResponse getNotice(@PathVariable Long noticeId) {
        return noticeService.getAdminNotice(noticeId);
    }

    @PostMapping("/notices")
    public NoticeResponse createNotice(@RequestBody NoticeRequest request,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return noticeService.createNotice(userDetails.getMemberId(), request);
    }

    @PatchMapping("/notices/{noticeId}")
    public NoticeResponse updateNotice(@PathVariable Long noticeId,
                                       @RequestBody NoticeRequest request) {
        return noticeService.updateNotice(noticeId, request);
    }

    @DeleteMapping("/notices/{noticeId}")
    public void deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
    }

    @GetMapping("/categories")
    public List<AdminCategoryResponse> getCategories(@RequestParam String type) {
        return adminCategoryService.getCategories(type);
    }

    @PostMapping("/categories")
    public AdminCategoryResponse createCategory(@RequestParam String type,
                                                @RequestBody AdminCategoryRequest request) {
        return adminCategoryService.createCategory(type, request.getName());
    }

    @PatchMapping("/categories/{id}")
    public AdminCategoryResponse updateCategory(@PathVariable Long id,
                                                @RequestParam String type,
                                                @RequestBody AdminCategoryRequest request) {
        return adminCategoryService.updateCategory(type, id, request.getName());
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id,
                               @RequestParam String type) {
        adminCategoryService.deleteCategory(type, id);
    }
}
