package com.roommate.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminActionLogItemResponse {

    @JsonProperty("admin_action_log_id")
    private final Long adminActionLogId;

    @JsonProperty("admin_id")
    private final Long adminId;

    @JsonProperty("admin_email")
    private final String adminEmail;

    @JsonProperty("admin_name")
    private final String adminName;

    @JsonProperty("action_type")
    private final String actionType;

    @JsonProperty("target_type")
    private final String targetType;

    @JsonProperty("target_id")
    private final Long targetId;

    @JsonProperty("action_detail")
    private final String actionDetail;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;
}
