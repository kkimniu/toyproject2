package com.roommate.domain.member.entity;

import java.time.LocalDateTime;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WorkTypeEntity {
	private Long workTypeId;
	private String workTypeName;
	private LocalDateTime createdAt;
}
