package com.roommate.domain.chat.repository;

import com.roommate.domain.chat.entity.ChatMessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageRepository {
    void save(ChatMessageEntity chatMessageEntity);

    List<ChatMessageEntity> findByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
