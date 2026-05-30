package com.roommate.domain.auth.controller;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.common.jwt.JwtCookieUtil;
import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.auth.dto.AuthTokenResult;
import com.roommate.domain.auth.dto.request.LoginRequest;
import com.roommate.domain.auth.dto.request.SignUpRequest;
import com.roommate.domain.auth.dto.response.LoginResponse;
import com.roommate.domain.auth.dto.response.SignUpResponse;
import com.roommate.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final JwtCookieUtil jwtCookieUtil;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = authService.signUp(signUpRequest);
        return ResponseEntity.status(201).body(signUpResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthTokenResult result = authService.login(loginRequest);
        jwtCookieUtil.addRefreshTokenCookie(response, result.getRefreshToken(), result.getRefreshMaxAgeSeconds());
        return ResponseEntity.ok(result.getLoginResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        AuthTokenResult result = authService.refreshToken(refreshToken);
        jwtCookieUtil.addRefreshTokenCookie(response, result.getRefreshToken(), result.getRefreshMaxAgeSeconds());
        return ResponseEntity.ok(result.getLoginResponse());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }
        authService.logout(userDetails.getMemberId());
        jwtCookieUtil.deleteRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }
}