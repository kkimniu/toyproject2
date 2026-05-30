package com.roommate.domain.chat.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.chat.dto.response.ChatRoomListItemResponse;
import com.roommate.domain.chat.dto.response.ChatRoomDetailResponse;
import com.roommate.domain.chat.entity.ChatRoomEntity;
import com.roommate.domain.chat.repository.ChatRoomRepository;
import com.roommate.domain.room.entity.RoomEntity;
import com.roommate.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final RoomRepository roomRepository;
    private final ChatRoomRepository chatRoomRepository;

    private void validateCreateRequest(Long roomId, Long currentMemberId) {
        if (currentMemberId == null) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }
        if (roomId == null || roomId <= 0) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
    }

    @Override
    @Transactional
    public Long getOrCreateChatRoom(Long roomId, Long currentMemberId) {

        validateCreateRequest(roomId, currentMemberId);

        RoomEntity roomEntity = roomRepository.findById(roomId);
        if (roomEntity == null) {
            throw new ApiException(ErrorCode.ROOM_NOT_FOUND);
        }
        Long ownerId = roomEntity.getMemberId();
        if (ownerId.equals(currentMemberId)) {
            throw new ApiException(ErrorCode.CHAT_SELF_CHAT_NOT_ALLOWED);
        }
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findByRoomAndUsers(roomId, ownerId, currentMemberId);
        if (chatRoomEntity != null) {
            chatRoomRepository.insertMember(chatRoomEntity.getChatRoomId(), ownerId);
            chatRoomRepository.insertMember(chatRoomEntity.getChatRoomId(), currentMemberId);
            boolean wasHidden = chatRoomRepository.isHiddenForMember(chatRoomEntity.getChatRoomId(), currentMemberId);
            chatRoomRepository.unhideForMember(chatRoomEntity.getChatRoomId(), currentMemberId);
            if (wasHidden) {
                chatRoomRepository.unhideForMember(chatRoomEntity.getChatRoomId(), ownerId);
            }
            return chatRoomEntity.getChatRoomId();
        }

        ChatRoomEntity newChatRoom = new ChatRoomEntity();
        newChatRoom.setRoomId(roomId);
        newChatRoom.setOwnerId(ownerId);
        newChatRoom.setPartnerId(currentMemberId);
        newChatRoom.setDeletedByOwner(false);
        newChatRoom.setDeletedByPartner(false);
        try {
            chatRoomRepository.insert(newChatRoom);
            chatRoomRepository.insertMember(newChatRoom.getChatRoomId(), ownerId);
            chatRoomRepository.insertMember(newChatRoom.getChatRoomId(), currentMemberId);
        } catch (DuplicateKeyException e) {
            ChatRoomEntity retry = chatRoomRepository.findByRoomAndUsers(roomId, ownerId, currentMemberId);
            if (retry != null) {
                chatRoomRepository.insertMember(retry.getChatRoomId(), ownerId);
                chatRoomRepository.insertMember(retry.getChatRoomId(), currentMemberId);
                boolean wasHidden = chatRoomRepository.isHiddenForMember(retry.getChatRoomId(), currentMemberId);
                chatRoomRepository.unhideForMember(retry.getChatRoomId(), currentMemberId);
                if (wasHidden) {
                    chatRoomRepository.unhideForMember(retry.getChatRoomId(), ownerId);
                }
                return retry.getChatRoomId();
            }
            throw new ApiException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }
        return newChatRoom.getChatRoomId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomListItemResponse> getMyChatRooms(Long currentMemberId) {
        if (currentMemberId == null) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }
        return chatRoomRepository.findMyChatRooms(currentMemberId);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomDetailResponse getMyChatRoom(Long chatRoomId, Long currentMemberId) {
        ChatRoomEntity chatRoom = getParticipantRoom(chatRoomId, currentMemberId);
        ChatRoomDetailResponse response = chatRoomRepository.findMyChatRoom(chatRoom.getChatRoomId(), currentMemberId);
        if (response == null) {
            throw new ApiException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
        return response;
    }

    @Override
    @Transactional
    public void markRead(Long chatRoomId, Long currentMemberId) {
        ChatRoomEntity chatRoom = getParticipantRoom(chatRoomId, currentMemberId);
        chatRoomRepository.markReadToLatest(chatRoom.getChatRoomId(), currentMemberId);
    }

    @Override
    @Transactional
    public void hideChatRoom(Long chatRoomId, Long currentMemberId) {
        ChatRoomEntity chatRoom = getParticipantRoom(chatRoomId, currentMemberId);
        chatRoomRepository.hideForMember(chatRoom.getChatRoomId(), currentMemberId);
    }

    @Override
    @Transactional
    public void updateNotificationsEnabled(Long chatRoomId, Long currentMemberId, boolean notificationsEnabled) {
        ChatRoomEntity chatRoom = getParticipantRoom(chatRoomId, currentMemberId);
        chatRoomRepository.updateNotificationsEnabled(chatRoom.getChatRoomId(), currentMemberId, notificationsEnabled);
    }

    private ChatRoomEntity getParticipantRoom(Long chatRoomId, Long currentMemberId) {
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
        if (!currentMemberId.equals(chatRoom.getOwnerId()) && !currentMemberId.equals(chatRoom.getPartnerId())) {
            throw new ApiException(ErrorCode.CHAT_ACCESS_DENIED);
        }
        return chatRoom;
    }
}
