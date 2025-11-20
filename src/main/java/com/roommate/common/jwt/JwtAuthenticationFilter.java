package com.roommate.common.jwt;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        //Http 헤더에서 'Authorization' 가져옴
        String bearerToken = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(JwtUtil.BEARER_PREFIX)) {
            try {
                String token = jwtUtil.substringToken(bearerToken);
                if (jwtUtil.validateToken(token)) {
                    Claims userInfo = jwtUtil.getUserInfoFromToken(token);
                    Long userId = Long.parseLong(userInfo.getSubject());
                    setAuthentication(userId);
                }
            } catch (Exception e) {
                log.error("JWT인증 처리 중 예외 발생 : " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * JWT가 유효하면 스프링  시큐리티에 등록하는 메서드
     */
    private void setAuthentication(Long userId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        MemberEntity memberEntity = memberRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("해당 id값으로 사용자를 찾을 수 없습니다. id=" + userId));
        UserDetailsImpl userDetails = new UserDetailsImpl(memberEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
