package com.roommate.domain.member.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PetEntity {
    private Long petId;
    private String petName;
    private LocalDateTime createdAt;
}
