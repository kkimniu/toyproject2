package com.roommate.domain.chat.repository;

import com.roommate.domain.chat.entity.ChatEntity;
import com.roommate.domain.chat.entity.ChatRoomListItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatRoomRepository {
    Optional<ChatEntity> findByRoomAndMembers(@Param("roomId") Long roomId,
                                              @Param("ownerId") Long ownerId,
                                              @Param("partnerId") Long partnerId);

    Optional<ChatEntity> findById(@Param("chatRoomId") Long chatRoomId);

    List<ChatRoomListItemEntity> findListByMemberId(@Param("memberId") Long memberId);

    void save(ChatEntity chatEntity);

    void markDeletedByMember(@Param("memberId") Long memberId);

    void updateLastMessage(@Param("chatRoomId") Long chatRoomId, @Param("messageId") Long messageId);
}
