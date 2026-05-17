package com.roommate.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
