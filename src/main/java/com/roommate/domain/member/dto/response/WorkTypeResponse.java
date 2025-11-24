package com.roommate.domain.member.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkTypeResponse {
    private Long workTypeId;
    private String workTypeName;
}
