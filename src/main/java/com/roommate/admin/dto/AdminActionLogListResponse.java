package com.roommate.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminActionLogListResponse {

    private final List<AdminActionLogItemResponse> items;

    private final int page;

    private final int size;

    @JsonProperty("total_count")
    private final long totalCount;

    @JsonProperty("total_pages")
    private final int totalPages;

    @JsonProperty("has_next")
    private final boolean hasNext;
}
