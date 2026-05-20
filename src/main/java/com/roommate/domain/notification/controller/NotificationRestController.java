package com.roommate.domain.notification.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.notification.dto.NotificationResponse;
import com.roommate.domain.notification.service.NotificationService;
import com.roommate.domain.report.dto.MyReportListItemResponse;
import com.roommate.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotificationService notificationService;
    private final ReportService reportService;

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return Map.of("unread_count", notificationService.countUnread(userDetails.getMemberId()));
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(@RequestParam(defaultValue = "5") int limit,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getRecentNotifications(userDetails.getMemberId(), limit);
    }

    @PatchMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationService.markAsRead(userDetails.getMemberId(), notificationId);
    }

    @GetMapping("/reports/{reportId}")
    public MyReportListItemResponse getReportNotificationDetail(@PathVariable Long reportId,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.getMyReport(userDetails.getMemberId(), reportId);
    }
}
