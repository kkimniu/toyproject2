package com.roommate.domain.member.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HobbyEntity {
    private Long hobbyId;
    private String hobbyName;
    private LocalDateTime createdAt;
}
