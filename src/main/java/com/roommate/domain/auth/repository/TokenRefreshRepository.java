package com.roommate.domain.auth.repository;

import com.roommate.domain.auth.entity.TokenRefreshEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface TokenRefreshRepository {

    void save(TokenRefreshEntity tokenRefreshEntity);

    Optional<TokenRefreshEntity> findByMemberId(@Param("memberId") Long memberId);

    Optional<TokenRefreshEntity> findById(@Param("tokenId") Long tokenId);

    void updateTokenRefresh(TokenRefreshEntity tokenRefreshEntity);

    void deleteByMemberId(@Param("memberId") Long memberId);
}
