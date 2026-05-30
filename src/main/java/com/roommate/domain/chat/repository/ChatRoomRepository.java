package com.roommate.domain.chat.repository;

import com.roommate.domain.chat.dto.response.ChatRoomListItemResponse;
import com.roommate.domain.chat.dto.response.ChatRoomDetailResponse;
import com.roommate.domain.chat.entity.ChatRoomEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ChatRoomRepository {
    void markDeletedByMember(@Param("memberId") Long memberId);

    ChatRoomEntity findByRoomAndUsers(@Param("roomId") Long roomId, @Param("ownerId") Long ownerId, @Param("partnerId") Long partnerId);

    void insert(ChatRoomEntity chatRoomEntity);

    ChatRoomEntity findById(@Param("chatRoomId") Long chatRoomId);

    void insertMember(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    void unhideForMember(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    boolean isHiddenForMember(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    List<ChatRoomListItemResponse> findMyChatRooms(@Param("memberId") Long memberId);

    ChatRoomDetailResponse findMyChatRoom(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    void updateLastMessage(@Param("chatRoomId") Long chatRoomId, @Param("messageId") Long messageId, @Param("sentAt") LocalDateTime sentAt);

    void markReadToLatest(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    void hideForMember(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    int adminMarkDeleted(@Param("chatRoomId") Long chatRoomId);

    int adminHideMembers(@Param("chatRoomId") Long chatRoomId);

    void updateNotificationsEnabled(
            @Param("chatRoomId") Long chatRoomId,
            @Param("memberId") Long memberId,
            @Param("notificationsEnabled") boolean notificationsEnabled
    );
}
