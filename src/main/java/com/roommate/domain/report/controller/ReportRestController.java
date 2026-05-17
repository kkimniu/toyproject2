package com.roommate.domain.report.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.report.dto.MyReportListItemResponse;
import com.roommate.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportRestController {

    private final ReportService reportService;

    @GetMapping("/me")
    public List<MyReportListItemResponse> getMyReports(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.getMyReports(userDetails.getMemberId());
    }
}
