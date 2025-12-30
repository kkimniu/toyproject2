package com.roommate.common.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class JwtCookieUtil {

    //  쿠키 이름은 상수/설정으로 통일 (프론트/백엔드 혼선 방지)
    @Value("${jwt.refresh.cookie.name:refresh_token}")
    private String refreshCookieName;

    // 로컬 개발에서는 secure=false가 필요할 수 있음(http 환경)
    @Value("${jwt.refresh.cookie.secure:false}")
    private boolean secure;

    //  React가 다른 Origin(예: localhost:3000)이라면 SameSite=None 필요
    // 단, SameSite=None은 Secure=true가 함께여야 브라우저에서 허용됨(HTTPS)
    @Value("${jwt.refresh.cookie.same-site:Lax}")
    private String sameSite;

    //  쿠키 Path를 refresh로 제한하면 공격면이 줄어듦
    @Value("${jwt.refresh.cookie.path:/api/auth/refresh}")
    private String path;

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, refreshToken)
                .httpOnly(true)  // JS로 접근 불가 (XSS 방어 핵심)
                .secure(secure)   // HTTPS 환경이면 true 권장
                .sameSite(sameSite)
                .path(path)
                .maxAge(maxAgeSeconds)
                .build();

        // ✅ Set-Cookie 헤더로 쿠키를 내려줌
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public String getRefreshCookieName() {
        return refreshCookieName;
    }
}
