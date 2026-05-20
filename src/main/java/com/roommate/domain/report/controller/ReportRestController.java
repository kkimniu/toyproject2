package com.roommate.domain.report.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.report.dto.MyReportListItemResponse;
import com.roommate.domain.report.dto.ReportRequest;
import com.roommate.domain.report.dto.ReportResponse;
import com.roommate.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportRestController {

    private final ReportService reportService;

    @PostMapping("/members/{memberId}")
    public ReportResponse reportMember(@PathVariable Long memberId,
                                       @Valid @RequestBody ReportRequest request,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.createMemberReport(userDetails.getMemberId(), memberId, request);
    }

    @PostMapping("/rooms/{roomId}")
    public ReportResponse reportRoom(@PathVariable Long roomId,
                                     @Valid @RequestBody ReportRequest request,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.createRoomReport(userDetails.getMemberId(), roomId, request);
    }

    @PostMapping("/chat-rooms/{chatRoomId}")
    public ReportResponse reportChat(@PathVariable Long chatRoomId,
                                     @Valid @RequestBody ReportRequest request,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.createChatReport(userDetails.getMemberId(), chatRoomId, request);
    }

    @GetMapping("/me")
    public List<MyReportListItemResponse> getMyReports(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.getMyReports(userDetails.getMemberId());
    }

    @GetMapping("/me/{reportId}")
    public MyReportListItemResponse getMyReport(@PathVariable Long reportId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.getMyReport(userDetails.getMemberId(), reportId);
    }
}
