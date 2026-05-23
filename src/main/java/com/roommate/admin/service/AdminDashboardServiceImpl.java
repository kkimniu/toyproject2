package com.roommate.admin.service;

import com.roommate.admin.dto.AdminDashboardSummaryResponse;
import com.roommate.admin.dto.AdminOperationAlertResponse;
import com.roommate.admin.dto.AdminReportTrendResponse;
import com.roommate.admin.dto.AdminSanctionCandidateResponse;
import com.roommate.domain.member.entity.MemberStatusEnum;
import com.roommate.domain.member.repository.MemberRepository;
import com.roommate.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private static final int SANCTION_CANDIDATE_REPORT_THRESHOLD = 3;
    private static final int SANCTION_CANDIDATE_LIMIT = 5;
    private static final int REPEAT_PENALTY_THRESHOLD = 3;
    private static final int REPORT_TREND_DAYS = 7;

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;

    @Override
    public AdminDashboardSummaryResponse getSummary() {
        long pendingReports = reportRepository.countReportsByStatusForAdmin("PENDING");
        long resolvedReports = reportRepository.countReportsByStatusForAdmin("RESOLVED");
        long sanctionCandidateCount = reportRepository.countSanctionCandidatesForAdmin(SANCTION_CANDIDATE_REPORT_THRESHOLD);
        long repeatPenaltyMembers = memberRepository.countRepeatPenaltyMembersForAdmin(REPEAT_PENALTY_THRESHOLD);
        List<AdminSanctionCandidateResponse> sanctionCandidates = reportRepository.findSanctionCandidatesForAdmin(
                SANCTION_CANDIDATE_REPORT_THRESHOLD,
                SANCTION_CANDIDATE_LIMIT
        );
        List<AdminOperationAlertResponse> operationAlerts = buildOperationAlerts(
                pendingReports,
                sanctionCandidateCount,
                repeatPenaltyMembers
        );
        List<AdminReportTrendResponse> reportTrends = reportRepository.findReportTrendsForAdmin(REPORT_TREND_DAYS);

        return new AdminDashboardSummaryResponse(
                memberRepository.countMembersForAdmin(null, null, null, null, null),
                memberRepository.countMembersByStatusForAdmin(MemberStatusEnum.BANNED),
                pendingReports,
                resolvedReports,
                sanctionCandidateCount,
                repeatPenaltyMembers,
                pendingReports + sanctionCandidateCount + repeatPenaltyMembers,
                sanctionCandidates,
                operationAlerts,
                reportTrends
        );
    }

    private List<AdminOperationAlertResponse> buildOperationAlerts(long pendingReports,
                                                                   long sanctionCandidateCount,
                                                                   long repeatPenaltyMembers) {
        List<AdminOperationAlertResponse> alerts = new ArrayList<>();
        if (pendingReports > 0) {
            alerts.add(new AdminOperationAlertResponse(
                    "PENDING_REPORT",
                    "대기 신고 확인 필요",
                    "아직 처리되지 않은 신고가 있습니다.",
                    pendingReports,
                    "reports"
            ));
        }
        if (sanctionCandidateCount > 0) {
            alerts.add(new AdminOperationAlertResponse(
                    "SANCTION_CANDIDATE",
                    "제재 후보 확인 필요",
                    "신고가 3건 이상 누적된 회원이 있습니다.",
                    sanctionCandidateCount,
                    "dashboard"
            ));
        }
        if (repeatPenaltyMembers > 0) {
            alerts.add(new AdminOperationAlertResponse(
                    "REPEAT_PENALTY",
                    "반복 정지 회원 확인 필요",
                    "정지 횟수가 3회 이상인 회원이 있습니다.",
                    repeatPenaltyMembers,
                    "members"
            ));
        }
        return alerts;
    }
}
