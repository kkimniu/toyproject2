package com.roommate.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberSmokingEnum {
    NON_SMOKER("NON_SMOKER"),
    SMOKER("SMOKER");

    private final String smokingType;
}
