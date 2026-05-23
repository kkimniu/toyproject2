package com.roommate.domain.community.repository;

import com.roommate.domain.community.entity.CommunityPostEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommunityPostRepository {
    long count(@Param("keyword") String keyword);

    List<CommunityPostEntity> findAll(@Param("keyword") String keyword,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);

    Optional<CommunityPostEntity> findById(@Param("communityPostId") Long communityPostId);

    int insert(CommunityPostEntity post);

    int update(CommunityPostEntity post);

    int softDelete(@Param("communityPostId") Long communityPostId,
                   @Param("memberId") Long memberId);

    int insertPostView(@Param("communityPostId") Long communityPostId,
                       @Param("memberId") Long memberId);

    int syncPostViewCount(@Param("communityPostId") Long communityPostId);
}
