package com.roommate.common.jwt;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.entity.MemberStatusEnum;
import com.roommate.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    /**
     * 요청이 들어올때 마다 실행되는 메서드
     * AUTHORIZATION_HEADER 헤더에서 JWT 추출
     * 토큰이 유효한지 검증
     * 유효하면 스프링 시큐리티에 등록
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 1) Bearer 파싱 (형식 오류면 예외)
            String token = jwtUtil.resolveBearerToken(authorizationHeader);

            // 2) Access 토큰 검증 + Claims 파싱 (서명/만료/typ=access 강제)
            Claims claims = jwtUtil.validateAndParseAccessClaims(token);

            // 3) memberId 추출
            Long memberId = jwtUtil.getMemberId(claims);

            // 4) SecurityContext 등록
            setAuthentication(memberId);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.debug("EXPIRED_JWT: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 필요하면 여기서 에러 바디(JSON)도 내려도 됨
            return;

        } catch (JwtException e) {
            // 서명 위조/형식 오류/typ 불일치 등
            log.warn("INVALID_JWT: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        } catch (Exception e) {
            // 예상 못한 장애
            log.error("JWT_FILTER_ERROR", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }

    /**
     * JWT가 유효하면 스프링  시큐리티에 등록하는 메서드
     */
    private void setAuthentication(Long memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        if (memberEntity.getDeleted() == 1 || memberEntity.getStatus() == MemberStatusEnum.DELETED) {
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }
        if (memberEntity.getStatus() == MemberStatusEnum.BANNED) {
            throw new ApiException(ErrorCode.MEMBER_BANNED);
        }
        UserDetailsImpl principal = new UserDetailsImpl(memberEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * JWT 인증 필터를 적용하지 않을 요청 경로를 지정하는 메서드.
     * - refresh 요청은 Access Token이 만료된 상태에서 호출될 수 있으므로
     * JWT 검증 없이 통과하도록 예외 처리한다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/api/auth/refresh");
    }
}
