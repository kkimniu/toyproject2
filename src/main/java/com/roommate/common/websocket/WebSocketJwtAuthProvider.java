package com.roommate.common.websocket;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.common.jwt.JwtUtil;
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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "WebSocketJwtAuthProvider")
public class WebSocketJwtAuthProvider {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    /**
     * STOMP CONNECT 헤더의 Authorization 값을 받아 Authentication 생성
     * - Access Token만 허용 (typ=access)
     */
    public Authentication authenticate(String authorizationHeader) {

        if (!StringUtils.hasText(authorizationHeader)) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }

        try {
            // 1) Bearer 파싱
            String token = jwtUtil.resolveBearerToken(authorizationHeader);

            // 2) Access 토큰 검증 + Claims 파싱
            Claims claims = jwtUtil.validateAndParseAccessClaims(token);

            // 3) memberId 추출
            Long memberId = jwtUtil.getMemberId(claims);

            // 4) 회원 조회
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
            if (member.getDeleted() == 1 || member.getStatus() == MemberStatusEnum.DELETED) {
                throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
            }
            if (member.getStatus() == MemberStatusEnum.BANNED) {
                throw new ApiException(ErrorCode.MEMBER_BANNED);
            }

            // 5) Principal 생성
            UserDetailsImpl principal = new UserDetailsImpl(member);

            // 6) Authentication 생성
            return new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );

        } catch (ExpiredJwtException e) {
            log.debug("EXPIRED_STOMP_JWT", e);
            throw new ApiException(ErrorCode.EXPIRED_TOKEN);

        } catch (JwtException e) {
            log.warn("INVALID_STOMP_JWT", e);
            throw new ApiException(ErrorCode.INVALID_AUTH_TOKEN);

        } catch (ApiException e) {
            throw e;

        } catch (Exception e) {
            log.error("STOMP_AUTH_ERROR", e);
            throw new ApiException(ErrorCode.INVALID_AUTH_TOKEN);
        }
    }
}
