package com.roommate.domain.chat.service;

import com.roommate.domain.chat.dto.ChatMessageResponse;
import com.roommate.domain.chat.dto.ChatResponse;
import com.roommate.domain.chat.dto.ChatRoomListItemResponse;

import java.util.List;

public interface ChatService {
    ChatResponse createOrGetChatRoom(Long requesterId, Long roomId, Long partnerId);

    List<ChatRoomListItemResponse> getChatRooms(Long memberId);

    List<ChatMessageResponse> getMessages(Long memberId, Long chatRoomId);

    ChatMessageResponse sendMessage(Long memberId, Long chatRoomId, String message);
}
