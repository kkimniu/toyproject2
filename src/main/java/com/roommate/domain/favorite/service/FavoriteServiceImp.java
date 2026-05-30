package com.roommate.domain.favorite.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.favorite.dto.response.FavoriteRoomResponse;
import com.roommate.domain.favorite.repository.FavoriteRepository;
import com.roommate.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImp implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public List<FavoriteRoomResponse> getMyFavoriteRooms(Long memberId) {
        return favoriteRepository.findMyFavoriteRooms(memberId);
    }

    @Override
    public void addFavorite(Long memberId, Long roomId) {
        if (roomRepository.findById(roomId) == null) {
            throw new ApiException(ErrorCode.ROOM_NOT_FOUND);
        }
        int exists = favoriteRepository.existsByMemberIdAndRoomId(memberId, roomId);
        if (exists > 0) {
            throw new ApiException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }
        favoriteRepository.save(memberId, roomId);
    }

    @Override
    @Transactional
    public void removeFavorite(Long memberId, Long roomId) {
        favoriteRepository.deleteByMemberIdAndRoomId(memberId, roomId);
    }
}
