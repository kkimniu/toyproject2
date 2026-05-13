package com.roommate.domain.chat.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.chat.dto.request.ChatNotificationSettingRequest;
import com.roommate.domain.chat.dto.request.ChatRoomCreateRequest;
import com.roommate.domain.chat.dto.response.ChatRoomListItemResponse;
import com.roommate.domain.chat.dto.response.ChatMessageResponse;
import com.roommate.domain.chat.dto.response.ChatRoomCreateResponse;
import com.roommate.domain.chat.service.ChatMessageService;
import com.roommate.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRestController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping
    public ChatRoomCreateResponse createOrGetChatRoom(@RequestBody ChatRoomCreateRequest chatRoomCreateRequest, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long chatRoomId = chatRoomService.getOrCreateChatRoom(chatRoomCreateRequest.getRoomId(), userDetails.getMemberId());
        return new ChatRoomCreateResponse(chatRoomId);
    }

    @GetMapping
    public List<ChatRoomListItemResponse> getMyChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getMyChatRooms(userDetails.getMemberId());
    }

    @GetMapping("/{chatRoomId}/messages")
    public List<ChatMessageResponse> getChatMessages(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMemberId();
        return chatMessageService.getMessages(chatRoomId, memberId);
    }

    @PatchMapping("/{chatRoomId}/read")
    public void markRead(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.markRead(chatRoomId, userDetails.getMemberId());
    }

    @DeleteMapping("/{chatRoomId}/me")
    public void hideChatRoom(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.hideChatRoom(chatRoomId, userDetails.getMemberId());
    }

    @PatchMapping("/{chatRoomId}/notifications")
    public void updateNotificationsEnabled(
            @PathVariable Long chatRoomId,
            @RequestBody ChatNotificationSettingRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        boolean enabled = request.getNotificationsEnabled() == null || request.getNotificationsEnabled();
        chatRoomService.updateNotificationsEnabled(chatRoomId, userDetails.getMemberId(), enabled);
    }
}
