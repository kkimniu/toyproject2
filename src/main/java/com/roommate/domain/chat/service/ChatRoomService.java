package com.roommate.domain.chat.service;

import com.roommate.domain.chat.dto.response.ChatRoomListItemResponse;
import com.roommate.domain.chat.dto.response.ChatRoomDetailResponse;

import java.util.List;

public interface ChatRoomService {
    Long getOrCreateChatRoom(Long roomId, Long currentMemberId);

    List<ChatRoomListItemResponse> getMyChatRooms(Long currentMemberId);
    ChatRoomDetailResponse getMyChatRoom(Long chatRoomId, Long currentMemberId);

    void markRead(Long chatRoomId, Long currentMemberId);

    void hideChatRoom(Long chatRoomId, Long currentMemberId);

    void updateNotificationsEnabled(Long chatRoomId, Long currentMemberId, boolean notificationsEnabled);
}
