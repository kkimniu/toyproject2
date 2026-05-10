package com.roommate.common.websocket;

import com.roommate.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final WebSocketJwtAuthProvider webSocketJwtAuthProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        // CONNECT 프레임에서만 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authorizationHeader =
                    accessor.getFirstNativeHeader(JwtUtil.AUTHORIZATION_HEADER);

            Authentication authentication =
                    webSocketJwtAuthProvider.authenticate(authorizationHeader);

            // 핵심: STOMP 세션에 사용자 정보 저장
            accessor.setUser(authentication);
        }

        return message;
    }
}
