package com.roommate.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
