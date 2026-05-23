package com.roommate.domain.community.repository;

import com.roommate.domain.community.entity.CommunityCommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommunityCommentRepository {
    List<CommunityCommentEntity> findByPostId(@Param("communityPostId") Long communityPostId);

    Optional<CommunityCommentEntity> findById(@Param("communityCommentId") Long communityCommentId);

    int insert(CommunityCommentEntity comment);

    int softDelete(@Param("communityCommentId") Long communityCommentId,
                   @Param("memberId") Long memberId);
}
