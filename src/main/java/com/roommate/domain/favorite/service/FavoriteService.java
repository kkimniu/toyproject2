package com.roommate.domain.favorite.service;

import com.roommate.domain.favorite.dto.response.FavoriteRoomResponse;

import java.util.List;

public interface FavoriteService {
    public List<FavoriteRoomResponse> getMyFavoriteRooms(Long memberId);

    public void addFavorite(Long memberId, Long roomId);

    public void removeFavorite(Long memberId, Long roomId);
}
