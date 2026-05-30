package com.roommate.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminCategoryResponse {
    private String type;
    private Long id;
    private String name;
    @JsonProperty("created_at")
    private Object createdAt;
}
