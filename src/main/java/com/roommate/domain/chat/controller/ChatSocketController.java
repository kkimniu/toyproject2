package com.roommate.domain.chat.controller;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.chat.dto.request.ChatMessageSendRequest;
import com.roommate.domain.chat.dto.response.ChatMessageResponse;
import com.roommate.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{chatRoomId}") // 클라이언트 send: /app/chat/{chatRoomId}
    public void sendMessage(@DestinationVariable Long chatRoomId, @Payload ChatMessageSendRequest request, Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }

        Long senderId = userDetails.getMemberId();

        ChatMessageResponse saved =
                chatMessageService.sendMessage(chatRoomId, senderId, request.getMessage());

        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, saved);
    }
}
