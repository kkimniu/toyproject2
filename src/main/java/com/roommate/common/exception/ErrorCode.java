package com.roommate.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DUPLICATE_EMAIL("E001","이미 사용중인 이메일입니다."),
    INVALID_PASSWORD("E002","이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_EMAIL("E003","이메일 또는 비밀번호가 올바르지 않습니다.");

    private final String code;
    private final String message;
}
