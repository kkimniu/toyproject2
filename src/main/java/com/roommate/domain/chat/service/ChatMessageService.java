package com.roommate.domain.chat.service;

import com.roommate.domain.chat.dto.response.ChatMessageResponse;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessageResponse> getMessages(Long chatRoomId, Long currentMemberId);
    ChatMessageResponse sendMessage(Long chatRoomId, Long senderId, String message);
}
