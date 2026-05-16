package com.roommate.admin.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class AdminMemberListItemResponse {
    private Long memberId;
    private String email;
    private String name;
    private MemberRoleEnum role;
    private MemberStatusEnum status;
    private LocalDateTime memberCreatedAt;
}
