package com.roommate.admin.service;

import com.roommate.admin.dto.AdminDashboardSummaryResponse;
import com.roommate.admin.dto.AdminDashboardSettingsRequest;
import com.roommate.admin.dto.AdminDashboardSettingsResponse;
import com.roommate.admin.dto.AdminOperationAlertResponse;
import com.roommate.admin.dto.AdminReportTrendResponse;
import com.roommate.admin.dto.AdminSanctionCandidateResponse;
import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.admin.repository.AdminSettingRepository;
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
    private static final String SANCTION_THRESHOLD_KEY = "SANCTION_CANDIDATE_REPORT_THRESHOLD";
    private static final String REPORT_TREND_DAYS_KEY = "REPORT_TREND_DAYS";
    private static final int DEFAULT_SANCTION_CANDIDATE_REPORT_THRESHOLD = 3;
    private static final int DEFAULT_REPORT_TREND_DAYS = 7;
    private static final int SANCTION_CANDIDATE_LIMIT = 5;
    private static final int REPEAT_PENALTY_THRESHOLD = 3;

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final AdminSettingRepository adminSettingRepository;

    @Override
    public AdminDashboardSummaryResponse getSummary(Integer requestedTrendDays) {
        AdminDashboardSettingsResponse settings = getSettings();
        int sanctionThreshold = settings.getSanctionCandidateReportThreshold();
        int trendDays = requestedTrendDays == null
                ? settings.getReportTrendDays()
                : validateReportTrendDays(requestedTrendDays);

        long pendingReports = reportRepository.countReportsByStatusForAdmin("PENDING");
        long resolvedReports = reportRepository.countReportsByStatusForAdmin("RESOLVED");
        long sanctionCandidateCount = reportRepository.countSanctionCandidatesForAdmin(sanctionThreshold);
        long repeatPenaltyMembers = memberRepository.countRepeatPenaltyMembersForAdmin(REPEAT_PENALTY_THRESHOLD);
        List<AdminSanctionCandidateResponse> sanctionCandidates = reportRepository.findSanctionCandidatesForAdmin(
                sanctionThreshold,
                SANCTION_CANDIDATE_LIMIT
        );
        List<AdminOperationAlertResponse> operationAlerts = buildOperationAlerts(
                pendingReports,
                sanctionCandidateCount,
                repeatPenaltyMembers,
                sanctionThreshold
        );
        List<AdminReportTrendResponse> reportTrends = reportRepository.findReportTrendsForAdmin(trendDays);

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
                reportTrends,
                new AdminDashboardSettingsResponse(sanctionThreshold, trendDays)
        );
    }

    @Override
    public AdminDashboardSettingsResponse getSettings() {
        return new AdminDashboardSettingsResponse(
                readIntSetting(SANCTION_THRESHOLD_KEY, DEFAULT_SANCTION_CANDIDATE_REPORT_THRESHOLD),
                readIntSetting(REPORT_TREND_DAYS_KEY, DEFAULT_REPORT_TREND_DAYS)
        );
    }

    @Override
    public AdminDashboardSettingsResponse updateSettings(AdminDashboardSettingsRequest request) {
        if (request == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        int sanctionThreshold = validateSanctionThreshold(request.getSanctionCandidateReportThreshold());
        int trendDays = validateReportTrendDays(request.getReportTrendDays());
        adminSettingRepository.upsert(SANCTION_THRESHOLD_KEY, String.valueOf(sanctionThreshold));
        adminSettingRepository.upsert(REPORT_TREND_DAYS_KEY, String.valueOf(trendDays));
        return new AdminDashboardSettingsResponse(sanctionThreshold, trendDays);
    }

    private List<AdminOperationAlertResponse> buildOperationAlerts(long pendingReports,
                                                                   long sanctionCandidateCount,
                                                                   long repeatPenaltyMembers,
                                                                   int sanctionThreshold) {
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
                    "신고가 " + sanctionThreshold + "건 이상 누적된 회원이 있습니다.",
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

    private int readIntSetting(String key, int defaultValue) {
        return adminSettingRepository.findValue(key)
                .map(value -> parseIntOrDefault(value, defaultValue))
                .orElse(defaultValue);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private int validateSanctionThreshold(Integer value) {
        if (value == null || value < 1 || value > 20) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        return value;
    }

    private int validateReportTrendDays(Integer value) {
        if (value == null || value < 7 || value > 90) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        return value;
    }
}
