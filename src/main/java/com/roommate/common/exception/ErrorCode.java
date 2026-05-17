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

    // ===== REFRESH TOKEN 관련 (추가) =====
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A006", "만료된 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A007", "리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "A008", "이미 사용된 리프레시 토큰입니다."),

    // 비밀번호 검증 관련
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "P001", "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "P002", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "P003", "이전 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),

    // ===== ADMIN / 관리자 관련 =====
    ADMIN_ONLY(HttpStatus.FORBIDDEN, "AD001", "관리자만 접근할 수 있습니다."),
    ADMIN_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "AD002", "관리 대상 회원이 존재하지 않습니다."),
    ADMIN_MEMBER_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AD003", "회원 삭제에 실패했습니다."),
    ADMIN_MEMBER_BAN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AD004", "회원 정지 처리에 실패했습니다."),
    ADMIN_SELF_STATUS_CHANGE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "AD005", "관리자는 본인 계정을 정지하거나 해제할 수 없습니다."),
    ADMIN_TARGET_ADMIN_NOT_ALLOWED(HttpStatus.FORBIDDEN, "AD006", "다른 관리자 계정은 정지하거나 해제할 수 없습니다."),
    ADMIN_MEMBER_STATUS_INVALID(HttpStatus.BAD_REQUEST, "AD007", "변경할 수 없는 회원 상태입니다."),
    ADMIN_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "AD008", "관리 대상 신고가 존재하지 않습니다."),
    ADMIN_REPORT_ALREADY_RESOLVED(HttpStatus.CONFLICT, "AD009", "이미 처리 완료된 신고입니다."),
    ADMIN_REPORT_STATUS_INVALID(HttpStatus.BAD_REQUEST, "AD010", "변경할 수 없는 신고 상태입니다."),
    ADMIN_REPORT_RESOLVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AD011", "신고 처리에 실패했습니다."),
    ADMIN_REPORT_RESOLUTION_TYPE_INVALID(HttpStatus.BAD_REQUEST, "AD012", "유효하지 않은 신고 처리 결과입니다."),
    ADMIN_REPORT_RESOLUTION_MESSAGE_REQUIRED(HttpStatus.BAD_REQUEST, "AD013", "신고자 안내 문구를 입력해야 합니다."),

    // ===== MEMBER / 회원 관련 =====
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M001", "이미 사용중인 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M002", "존재하지 않는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "M003", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "M004", "이메일 형식이 올바르지 않습니다."),
    MEMBER_DEACTIVATED(HttpStatus.FORBIDDEN, "M005", "탈퇴한 회원입니다."),
    MEMBER_BANNED(HttpStatus.FORBIDDEN, "M006", "정지된 회원입니다."),

    // ===== WORK_TYPE / 근무 유형 관련 =====
    WORK_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "존재하지 않는 근무 유형입니다."),
    INVALID_WORK_TYPE(HttpStatus.BAD_REQUEST, "W002", "유효하지 않은 근무 유형 값입니다."),

    // ===== FILE / 업로드 공통 =====
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "F001", "업로드할 파일이 존재하지 않습니다."),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "F002", "업로드 파일이 비어 있습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F003", "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F004", "파일 삭제에 실패했습니다."),
    FILE_PATH_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "F005", "파일 저장 경로가 올바르지 않습니다."),
    FILE_ALREADY_USED(HttpStatus.CONFLICT, "F006", "이미 사용 중인 파일입니다."),

    // ===== IMAGE / 이미지 업로드 =====
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "IMG001", "이미지 파일만 업로드할 수 있습니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "IMG002", "이미지 파일 용량이 허용 범위를 초과했습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMG003", "이미지를 찾을 수 없습니다."),
    IMAGE_PROCESS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMG004", "이미지 처리 중 오류가 발생했습니다."),

    // ===== ROOM / 방 관련 =====
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "R101", "존재하지 않는 방입니다."),
    ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "R102", "해당 방에 대한 권한이 없습니다."),
    ROOM_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "R103", "이미 삭제된 방입니다."),
    ROOM_STATUS_INVALID(HttpStatus.BAD_REQUEST, "R104", "유효하지 않은 방 상태입니다."),
    ROOM_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R105", "방 등록 중 오류가 발생했습니다."),
    ROOM_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R106", "방 수정 중 오류가 발생했습니다."),
    ROOM_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R107", "방 삭제 중 오류가 발생했습니다."),
    ROOM_STATUS_CHANGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R108", "방 상태 변경에 실패했습니다."),
    INVALID_ROOM_LOCATION(HttpStatus.BAD_REQUEST, "R109", "방 위치 정보가 올바르지 않습니다."),
    INVALID_CHAT_ROOM_REQUEST(HttpStatus.BAD_REQUEST, "R110", "채팅방 생성 요청이 올바르지 않습니다."),

    // ===== ROOM IMAGE / 방 이미지 관련 =====
    ROOM_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "방 이미지가 존재하지 않습니다."),
    ROOM_IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "I002", "방 이미지 업로드에 실패했습니다."),
    ROOM_IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "I003", "방 이미지 삭제에 실패했습니다."),
    ROOM_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "I004", "방 이미지는 최대 허용 개수를 초과할 수 없습니다."),

    // ===== FAVORITE / 찜 관련 =====
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "F001", "이미 찜한 방입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "F002", "찜한 내역이 존재하지 않습니다."),
    FAVORITE_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F003", "찜 등록에 실패했습니다."),
    FAVORITE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F004", "찜 삭제에 실패했습니다."),

    // ===== CHAT / 채팅 관련 =====
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "채팅방이 존재하지 않습니다."),
    CHAT_ROOM_ALREADY_EXISTS(HttpStatus.CONFLICT, "C002", "이미 존재하는 채팅방입니다."),
    CHAT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C003", "채팅방 접근 권한이 없습니다."),
    CHAT_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "메시지 전송에 실패했습니다."),
    CHAT_ROOM_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C005", "채팅방 삭제에 실패했습니다."),
    CHAT_SELF_CHAT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "C006", "본인에게는 문의할 수 없습니다."),
    CHAT_NOT_PARTICIPANT(HttpStatus.FORBIDDEN, "C007", "채팅방 참여자가 아닙니다."),
    CHAT_MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "C008", "메시지 내용이 비어 있습니다."),
    CHAT_MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "C009", "메시지는 500자 이하만 가능합니다."),

    // ===== REPORT / 신고 관련 =====
    REPORT_ALREADY_SUBMITTED(HttpStatus.CONFLICT, "RP001", "이미 신고한 대상입니다."),
    REPORT_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "RP002", "신고 대상이 존재하지 않습니다."),
    REPORT_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RP003", "신고 처리 중 오류가 발생했습니다."),

    // ===== MAP / 지도 조회 관련 =====
    INVALID_MAP_BOUNDARY(HttpStatus.BAD_REQUEST, "MAP001", "지도 좌표 범위가 올바르지 않습니다."),
    MAP_DATA_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAP002", "지도 데이터 조회에 실패했습니다."),

    // ===== KAKAO API / 외부 API 연동 =====
    KAKAO_API_CALL_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "K001", "카카오 API 호출에 실패했습니다."),
    KAKAO_ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "K002", "주소에 해당하는 좌표를 찾을 수 없습니다."),
    KAKAO_API_RESPONSE_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "K003", "카카오 API 응답 처리 중 오류가 발생했습니다."),

    // ===== INPUT / 요청 데이터 관련 =====
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "R001", "잘못된 요청입니다."),
    MISSING_REQUIRED_VALUE(HttpStatus.BAD_REQUEST, "R002", "필수 입력값이 누락되었습니다."),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "R003", "유효하지 않은 값입니다."),

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
