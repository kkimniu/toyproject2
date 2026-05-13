package com.roommate.domain.chat.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.chat.dto.ChatMessageRequest;
import com.roommate.domain.chat.dto.ChatMessageResponse;
import com.roommate.domain.chat.dto.ChatRoomListItemResponse;
import com.roommate.domain.chat.dto.ChatResquest;
import com.roommate.domain.chat.dto.ChatResponse;
import com.roommate.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<java.util.List<ChatRoomListItemResponse>> getChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(chatService.getChatRooms(userDetails.getMemberId()));
    }

    @PostMapping
    public ResponseEntity<ChatResponse> createOrGetChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestBody ChatResquest request) {
        ChatResponse response = chatService.createOrGetChatRoom(
                userDetails.getMemberId(),
                request.getRoomId(),
                request.getPartnerId()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<java.util.List<ChatMessageResponse>> getMessages(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                           @PathVariable Long chatRoomId) {
        return ResponseEntity.ok(chatService.getMessages(userDetails.getMemberId(), chatRoomId));
    }

    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @PathVariable Long chatRoomId,
                                                           @RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(userDetails.getMemberId(), chatRoomId, request.getMessage()));
    }
}
