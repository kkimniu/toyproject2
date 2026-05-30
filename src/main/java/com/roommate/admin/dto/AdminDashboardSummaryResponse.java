package com.roommate.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminDashboardSummaryResponse {

    @JsonProperty("total_members")
    private final long totalMembers;

    @JsonProperty("banned_members")
    private final long bannedMembers;

    @JsonProperty("pending_reports")
    private final long pendingReports;

    @JsonProperty("resolved_reports")
    private final long resolvedReports;

    @JsonProperty("sanction_candidate_count")
    private final long sanctionCandidateCount;

    @JsonProperty("repeat_penalty_members")
    private final long repeatPenaltyMembers;

    @JsonProperty("action_required_count")
    private final long actionRequiredCount;

    @JsonProperty("sanction_candidates")
    private final List<AdminSanctionCandidateResponse> sanctionCandidates;

    @JsonProperty("operation_alerts")
    private final List<AdminOperationAlertResponse> operationAlerts;

    @JsonProperty("report_trends")
    private final List<AdminReportTrendResponse> reportTrends;

    @JsonProperty("settings")
    private final AdminDashboardSettingsResponse settings;
}
