package com.roommate.domain.favorite.repository;

import com.roommate.domain.favorite.dto.response.FavoriteRoomResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteRepository {
    void deleteByMemberId(@Param("memberId") Long memberId);

    List<FavoriteRoomResponse> findMyFavoriteRooms(@Param("memberId") Long memberId);

    int existsByMemberIdAndRoomId(@Param("memberId") Long memberId, @Param("roomId") Long roomId);

    void save(@Param("memberId") Long memberId, @Param("roomId") Long roomId);

    void deleteByMemberIdAndRoomId(@Param("memberId") Long memberId, @Param("roomId") Long roomId);
}
