package com.roommate.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberDrinkingEnum {
    NONE("NONE"),
    SOCIAL("SOCIAL"),
    OFTEN("OFTEN");

    private final String drinkingType;
}
