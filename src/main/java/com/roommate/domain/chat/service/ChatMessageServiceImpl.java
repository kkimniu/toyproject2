package com.roommate.domain.chat.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.chat.dto.response.ChatMessageResponse;
import com.roommate.domain.chat.entity.ChatMessageEntity;
import com.roommate.domain.chat.entity.ChatRoomEntity;
import com.roommate.domain.chat.repository.ChatMessageRepository;
import com.roommate.domain.chat.repository.ChatRoomRepository;
import com.roommate.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationRepository notificationRepository;


    @Override
    @Transactional
    public ChatMessageResponse sendMessage(Long chatRoomId, Long senderId, String message) {

        if (senderId == null) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }
        if (chatRoomId == null || chatRoomId <= 0) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        String trimmed = (message == null) ? "" : message.trim();
        if (trimmed.isEmpty()) {
            throw new ApiException(ErrorCode.CHAT_MESSAGE_EMPTY);
        }
        if (trimmed.length() > 500) {
            throw new ApiException(ErrorCode.CHAT_MESSAGE_TOO_LONG);
        }

        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom == null) {
            throw new ApiException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        boolean isParticipant =
                senderId.equals(chatRoom.getOwnerId()) || senderId.equals(chatRoom.getPartnerId());

        if (!isParticipant) {
            throw new ApiException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setChatRoomId(chatRoomId);
        entity.setSenderId(senderId);
        entity.setMessage(trimmed);
        entity.setDeletedBySender(false);
        entity.setDeletedByReceiver(false);

        entity.setSentAt(LocalDateTime.now());
        chatMessageRepository.insert(entity);
        chatRoomRepository.updateLastMessage(chatRoomId, entity.getMessageId(), entity.getSentAt());

        Long receiverId = senderId.equals(chatRoom.getOwnerId()) ? chatRoom.getPartnerId() : chatRoom.getOwnerId();
        notificationRepository.insertChatNotificationIfEnabled(receiverId, chatRoomId, trimmed);

        return new ChatMessageResponse(
                entity.getMessageId(),
                chatRoomId,
                senderId,
                trimmed,
                entity.getSentAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long chatRoomId, Long currentMemberId) {

        if (currentMemberId == null) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }
        if (chatRoomId == null || chatRoomId <= 0) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom == null) {
            throw new ApiException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
        // 참여자 검증
        if (!currentMemberId.equals(chatRoom.getOwnerId()) && !currentMemberId.equals(chatRoom.getPartnerId())) {
            throw new ApiException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        List<ChatMessageEntity> messages = chatMessageRepository.findVisibleByChatRoomId(chatRoomId, currentMemberId);

        return messages.stream().map(m -> new ChatMessageResponse(
                m.getMessageId(),
                m.getChatRoomId(),
                m.getSenderId(),
                m.getMessage(),
                m.getSentAt()
        )).toList();
    }
}
