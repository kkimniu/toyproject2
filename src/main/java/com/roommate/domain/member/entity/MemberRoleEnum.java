package com.roommate.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRoleEnum {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN");
	
	private final String authority;
}
