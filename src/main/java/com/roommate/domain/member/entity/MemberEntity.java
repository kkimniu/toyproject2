package com.roommate.domain.member.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MemberEntity {

    private Long memberId;
    private Long workTypeId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String photoUrl;
    @Builder.Default
    private MemberRoleEnum role = MemberRoleEnum.USER;
    @Builder.Default
    private int reportCount = 0;
    @Builder.Default
    private int deleted = 0;
    @Builder.Default
    private LocalDateTime memberCreatedAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime memberUpdatedAt = LocalDateTime.now();
    private String sleepTime;
    @Builder.Default
    private MemberSmokingEnum smoking = MemberSmokingEnum.NON_SMOKER;
    @Builder.Default
    private MemberDrinkingEnum drinking = MemberDrinkingEnum.NONE;
    private String mbti;
}
