package com.roommate.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== AUTH / 인증 관련 =====
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "A001", "로그인이 필요한 요청입니다."),
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 인증 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A004", "접근 권한이 없습니다."),

    // ===== MEMBER / 회원 관련 =====
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M001", "이미 사용중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "M002", "이미 사용중인 닉네임입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M003", "존재하지 않는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "M004", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "M005", "이메일 형식이 올바르지 않습니다."),
    MEMBER_DEACTIVATED(HttpStatus.FORBIDDEN, "M006", "탈퇴한 회원입니다."),

    // ===== WORK_TYPE / 근무 유형 관련 =====
    WORK_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "존재하지 않는 근무 유형입니다."),
    INVALID_WORK_TYPE(HttpStatus.BAD_REQUEST, "W002", "유효하지 않은 근무 유형 값입니다."),

    // ===== INPUT / 요청 데이터 관련 =====
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "R001", "잘못된 요청입니다."),
    MISSING_REQUIRED_VALUE(HttpStatus.BAD_REQUEST, "R002", "필수 입력값이 누락되었습니다."),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "R003", "유효하지 않은 값입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "R004", "파일 업로드에 실패했습니다."),

    // ===== BUSINESS / 리소스 관련 =====
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "요청한 리소스를 찾을 수 없습니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "B002", "이미 존재하는 데이터입니다."),
    NOT_ALLOWED_OPERATION(HttpStatus.FORBIDDEN, "B003", "해당 요청은 허용되지 않습니다."),
    RESOURCE_IN_USE(HttpStatus.CONFLICT, "B004", "다른 리소스에서 사용 중이라 삭제할 수 없습니다."),

    // ===== SYSTEM / 서버 관련 =====
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "S002", "현재 요청을 처리할 수 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S003", "데이터베이스 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
