package com.roommate.domain.auth.service;


import com.roommate.domain.auth.dto.AuthTokenResult;
import com.roommate.domain.auth.dto.request.LoginRequest;
import com.roommate.domain.auth.dto.request.RefreshTokenRequest;
import com.roommate.domain.auth.dto.request.SignUpRequest;
import com.roommate.domain.auth.dto.response.LoginResponse;
import com.roommate.domain.auth.dto.response.SignUpResponse;

public interface AuthService {

    /**
     * @param email 이메일 중복검사
     */
    void validateEmailDuplication(String email);

    /**
     * 회원 가입
     */
    SignUpResponse signUp(SignUpRequest signUpRequest);

    /**
     * 비밀번호와 이메일 로그인 메서드
     */
    AuthTokenResult login(LoginRequest loginRequest);

    /**
     * 비밀 번호 검증
     */
    void validatePassword(String requestPassword, String memberPassword);

    /**
     * Access Token 재발급
     */
    AuthTokenResult refreshToken(String refreshToken);

    /**
     * 로그아웃
     */
    void logout(Long memberId);

}
