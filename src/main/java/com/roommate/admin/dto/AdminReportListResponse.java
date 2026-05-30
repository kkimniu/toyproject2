package com.roommate.admin.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class AdminReportListResponse {
    private List<AdminReportListItemResponse> items;
    private int page;
    private int size;
    private long totalCount;
    private int totalPages;
    private boolean hasNext;
}
