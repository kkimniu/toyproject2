package com.roommate.admin.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class AdminSanctionCandidateResponse {
    private Long memberId;
    private String email;
    private String name;
    private String status;
    private int banCount;
    private long totalReports;
    private long pendingReports;
    private long acceptedReports;
    private LocalDateTime lastReportedAt;
}
