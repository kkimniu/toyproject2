package com.roommate.common.websocket;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.chat.entity.ChatRoomEntity;
import com.roommate.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatSubscribeInterceptor implements ChannelInterceptor {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        // SUBSCRIBE 요청일 때만 검사
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            String destination = accessor.getDestination();

            if (destination != null && destination.startsWith("/topic/chat/")) {

                String chatRoomIdText = destination.substring("/topic/chat/".length());
                if (!chatRoomIdText.matches("\\d+")) {
                    throw new IllegalArgumentException("Invalid chat room destination");
                }

                Long chatRoomId = Long.valueOf(chatRoomIdText);

                Authentication authentication =
                        (Authentication) accessor.getUser();

                if (authentication == null) {
                    throw new IllegalArgumentException("Unauthorized subscribe");
                }

                if (!(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
                    throw new IllegalArgumentException("Invalid websocket principal");
                }

                Long memberId = userDetails.getMemberId();

                ChatRoomEntity chatRoom =
                        chatRoomRepository.findById(chatRoomId);

                if (chatRoom == null) {
                    throw new IllegalArgumentException("Chat room not found");
                }

                boolean isParticipant =
                        memberId.equals(chatRoom.getOwnerId()) ||
                                memberId.equals(chatRoom.getPartnerId());

                if (!isParticipant) {
                    throw new IllegalArgumentException("Access denied");
                }
            }
        }

        return message;
    }
}
