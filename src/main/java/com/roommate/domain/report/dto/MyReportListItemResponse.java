package com.roommate.domain.report.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class MyReportListItemResponse {
    private Long reportId;
    private Long targetMemberId;
    private String targetMemberEmail;
    private String targetMemberName;
    private String reason;
    private String status;
    private String resolutionType;
    private String resolutionMessage;
    private LocalDateTime processedAt;
    private LocalDateTime reportCreatedAt;
}
