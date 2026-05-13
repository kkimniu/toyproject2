package com.roommate.domain.chat.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.chat.dto.ChatMessageResponse;
import com.roommate.domain.chat.dto.ChatResponse;
import com.roommate.domain.chat.dto.ChatRoomListItemResponse;
import com.roommate.domain.chat.entity.ChatEntity;
import com.roommate.domain.chat.entity.ChatMessageEntity;
import com.roommate.domain.chat.entity.ChatRoomListItemEntity;
import com.roommate.domain.chat.repository.ChatMessageRepository;
import com.roommate.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImp implements ChatService{
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public ChatResponse createOrGetChatRoom(Long requesterId, Long roomId, Long partnerId) {
        if (requesterId == null || roomId == null || partnerId == null) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }
        if (requesterId.equals(partnerId)) {
            throw new ApiException(ErrorCode.NOT_ALLOWED_OPERATION);
        }

        ChatEntity chatRoom = chatRoomRepository.findByRoomAndMembers(roomId, requesterId, partnerId)
                .orElseGet(() -> createChatRoom(requesterId, roomId, partnerId));

        return new ChatResponse(chatRoom.getChatRoomId(), chatRoom.getRoomId(), partnerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomListItemResponse> getChatRooms(Long memberId) {
        if (memberId == null) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }
        return chatRoomRepository.findListByMemberId(memberId)
                .stream()
                .map(this::toRoomListItemResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long memberId, Long chatRoomId) {
        ChatEntity chatRoom = getAccessibleChatRoom(memberId, chatRoomId);
        return chatMessageRepository.findByChatRoomId(chatRoom.getChatRoomId())
                .stream()
                .map(message -> toMessageResponse(message, memberId))
                .toList();
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(Long memberId, Long chatRoomId, String message) {
        ChatEntity chatRoom = getAccessibleChatRoom(memberId, chatRoomId);
        if (message == null || message.isBlank()) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }

        ChatMessageEntity chatMessage = new ChatMessageEntity();
        chatMessage.setChatRoomId(chatRoom.getChatRoomId());
        chatMessage.setSenderId(memberId);
        chatMessage.setMessage(message.trim());
        chatMessageRepository.save(chatMessage);
        chatRoomRepository.updateLastMessage(chatRoom.getChatRoomId(), chatMessage.getMessageId());

        return chatMessageRepository.findByChatRoomId(chatRoom.getChatRoomId())
                .stream()
                .filter(saved -> saved.getMessageId().equals(chatMessage.getMessageId()))
                .findFirst()
                .map(saved -> toMessageResponse(saved, memberId))
                .orElseGet(() -> toMessageResponse(chatMessage, memberId));
    }

    private ChatEntity getAccessibleChatRoom(Long memberId, Long chatRoomId) {
        if (memberId == null || chatRoomId == null) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }
        ChatEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        if (!memberId.equals(chatRoom.getOwnerId()) && !memberId.equals(chatRoom.getPartnerId())) {
            throw new ApiException(ErrorCode.CHAT_ACCESS_DENIED);
        }
        return chatRoom;
    }

    private ChatMessageResponse toMessageResponse(ChatMessageEntity entity, Long currentMemberId) {
        return new ChatMessageResponse(
                entity.getMessageId(),
                entity.getChatRoomId(),
                entity.getSenderId(),
                entity.getSenderName(),
                entity.getMessage(),
                entity.getSentAt(),
                entity.getSenderId() != null && entity.getSenderId().equals(currentMemberId)
        );
    }

    private ChatRoomListItemResponse toRoomListItemResponse(ChatRoomListItemEntity entity) {
        return new ChatRoomListItemResponse(
                entity.getChatRoomId(),
                entity.getRoomId(),
                entity.getRoomTitle(),
                entity.getPartnerId(),
                entity.getPartnerName(),
                entity.getPartnerPhotoUrl(),
                entity.getLastMessage(),
                entity.getLastMessageAt()
        );
    }

    private ChatEntity createChatRoom(Long requesterId, Long roomId, Long partnerId) {
        ChatEntity chatRoom = new ChatEntity();
        chatRoom.setRoomId(roomId);
        chatRoom.setOwnerId(requesterId);
        chatRoom.setPartnerId(partnerId);
        chatRoom.setDeletedByOwner(0);
        chatRoom.setDeletedByPartner(0);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
}
