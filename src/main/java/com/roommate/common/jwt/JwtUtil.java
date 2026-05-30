package com.roommate.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.roommate.domain.member.entity.MemberRoleEnum;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j(topic = "JwtUtil")
public class JwtUtil {

    // HTTP 요청 헤더에서 JWT를 담는 데 사용될 키
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // JWT 토큰 값 앞에 붙는 접두사 (Bearer 스킴)
    public static final String BEARER_PREFIX = "Bearer ";

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYP = "typ";
    private static final String CLAIM_JTI = "jti";

    private static final String TYP_ACCESS = "access";
    private static final String TYP_REFRESH = "refresh";


    @Value("${jwt.secret.key}")
    private String secretKeyBase64; // application.properties에서 주입받은 비밀 키

    @Getter
    @Value("${jwt.access.expiration.time}")
    private long accessExpirationTime;

    @Getter
    @Value("${jwt.refresh.expiration.time}")
    private long refreshExpirationTime;

    private SecretKey key; // JWT 서명에 사용할 키 객체

    // @PostConstruct: 의존성 주입이 완료된 후 실행되는 초기화 메서드
    //이 메서드가 없으면 key가 null이라 서명/검증 시 NullPointerException.
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long memberId, MemberRoleEnum roleEnum) {
        return buildToken(memberId, roleEnum, accessExpirationTime, TYP_ACCESS);
    }

    public String createRefreshToken(Long memberId, MemberRoleEnum roleEnum) {
        return buildToken(memberId, roleEnum, refreshExpirationTime, TYP_REFRESH);
    }

    /**
     * 사용자 고유번호를 받아 JWT를 생성하는 메서드
     */
    private String buildToken(Long memberId, MemberRoleEnum role, long expirationMillis, String typ) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 토큰의 주체(사용자 이름) 설정
                .issuedAt(now) // 토큰 발급 시간 설정
                .expiration(expirationDate) // 토큰 만료 시간 설정
                .claim(CLAIM_ROLE, role.getAuthority())
                .claim(CLAIM_TYP, typ)
                .claim(CLAIM_JTI, UUID.randomUUID().toString())
                .signWith(key) // 사용할 암호화 알고리즘과 키로 서명 , 0.12.x에서는 key만 넘기면 alg는 자동 결정됨(HS256
                .compact(); // JWT 문자열로 압축
    }

    // =========================
    // Header -> Token 추출
    // =========================

    public String resolveBearerToken(String authorizationHeaderValue) {
        if (!StringUtils.hasText(authorizationHeaderValue) || !authorizationHeaderValue.startsWith(BEARER_PREFIX)) {
            throw new JwtException("INVALID_AUTH_HEADER");
        }
        return authorizationHeaderValue.substring(BEARER_PREFIX.length());
    }

    // =========================
    // 검증(예외 기반) + 파싱
    // =========================

    /**
     * Access 토큰 검증(서명/만료 + typ=access 강제)
     * 실패 시 JwtException 계열 예외 발생
     */
    public Claims validateAndParseAccessClaims(String token) {
        Claims claims = parseClaims(token);
        validateTyp(claims, TYP_ACCESS);
        return claims;
    }

    /**
     * Refresh 토큰 검증(서명/만료 + typ=refresh 강제)
     * 실패 시 JwtException 계열 예외 발생
     */
    public Claims validateAndParseRefreshClaims(String token) {
        Claims claims = parseClaims(token);
        validateTyp(claims, TYP_REFRESH);
        return claims;
    }

    /**
     * 공통 파싱(여기서 서명/만료 검증이 함께 수행됨)
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 서명/형식 문제는 WARN이 보통(공격/오입력)
            log.warn("INVALID_JWT_SIGNATURE_OR_FORMAT", e);
            throw new JwtException("INVALID_JWT", e);
        } catch (ExpiredJwtException e) {
            // 만료는 흔한 케이스라 DEBUG/INFO 정책을 팀에 맞춰 선택
            log.debug("EXPIRED_JWT");
            throw e; // 컨트롤러/필터에서 만료 응답(401) 분기하기 좋게 그대로 던짐
        } catch (UnsupportedJwtException e) {
            log.warn("UNSUPPORTED_JWT", e);
            throw new JwtException("UNSUPPORTED_JWT", e);
        } catch (IllegalArgumentException e) {
            log.warn("EMPTY_JWT", e);
            throw new JwtException("EMPTY_JWT", e);
        }
    }

    private void validateTyp(Claims claims, String expectedTyp) {
        String typ = claims.get(CLAIM_TYP, String.class);

        if (!expectedTyp.equals(typ)) {
            throw new JwtException("INVALID_TOKEN_TYPE");
        }
    }

    // =========================
    // Claims 접근 헬퍼
    // =========================

    public Long getMemberId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public String getRole(Claims claims) {
        return claims.get(CLAIM_ROLE, String.class);
    }

    public String getJti(Claims claims) {
        return claims.get(CLAIM_JTI, String.class);
    }

    // =========================
    //  boolean 검증이 필요한 곳을 위한 래퍼
    // =========================

    public boolean isValidAccessToken(String token) {
        try {
            validateAndParseAccessClaims(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isValidRefreshToken(String token) {
        try {
            validateAndParseRefreshClaims(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
