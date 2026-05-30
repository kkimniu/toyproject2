package com.roommate.domain.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthViewController {

    // 로그인 테스트 화면
    @GetMapping("/login-test")
    public String loginTestPage() {
        // /WEB-INF/views/auth/login-test.jsp 로 forward
        return "auth/login-test";
    }

    // 내 정보 + 자동 재발급 테스트 화면
    @GetMapping("/me-test")
    public String meTestPage() {
        // /WEB-INF/views/auth/me-test.jsp 로 forward
        return "auth/me-test";
    }

    // 로그인 화면
    @GetMapping("/login")
    public String loginPage() {
        // /WEB-INF/views/auth/login.jsp 로 forward
        return "auth/login";
    }
}