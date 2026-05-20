package com.roommate.domain.notice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class NoticeResponse {
    private Long noticeId;
    private String title;
    private String content;
    private boolean pinned;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
