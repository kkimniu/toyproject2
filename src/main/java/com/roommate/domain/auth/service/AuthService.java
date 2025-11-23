package com.roommate.domain.auth.service;


import com.roommate.domain.auth.dto.request.LoginRequest;
import com.roommate.domain.auth.dto.request.SignUpRequest;
import com.roommate.domain.auth.dto.response.LoginResponse;
import com.roommate.domain.auth.dto.response.SignUpResponse;
import com.roommate.domain.member.entity.MemberEntity;

import java.lang.reflect.Member;

public interface AuthService {

    /**
     * @param email 이메일 중복검사
     */
    public void validateEmailDuplication(String email);

    /**
     * 회원 가입
     */
    public SignUpResponse signUp(SignUpRequest signUpRequest);

    /**
     * 비밀번호와 이메일 로그인 메서드
     */
    public LoginResponse login(LoginRequest loginRequest);

    /**
     * 비밀 번호 검증
     */
    public void validatePassword(String requestPassword, String memberPassword);

}
