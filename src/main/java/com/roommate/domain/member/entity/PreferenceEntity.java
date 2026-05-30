package com.roommate.domain.member.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PreferenceEntity {
    private Long preferenceId;
    private String preferenceName;
    private LocalDateTime createdAt;
}
