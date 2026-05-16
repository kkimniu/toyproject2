package com.roommate.admin.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class AdminReportListItemResponse {
    private Long reportId;
    private Long targetMemberId;
    private String targetMemberEmail;
    private String targetMemberName;
    private Long reporterId;
    private String reporterEmail;
    private String reporterName;
    private String reason;
    private String status;
    private LocalDateTime reportCreatedAt;
}
