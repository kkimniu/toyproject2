package com.roommate.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeListResponse {
    private List<NoticeResponse> items;
    private int page;
    private int size;
    @JsonProperty("total_count")
    private long totalCount;
    @JsonProperty("total_pages")
    private int totalPages;
}
