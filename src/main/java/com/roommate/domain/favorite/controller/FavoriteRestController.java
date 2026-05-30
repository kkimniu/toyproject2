package com.roommate.domain.favorite.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.favorite.dto.response.FavoriteRoomResponse;
import com.roommate.domain.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteRestController {

    private final FavoriteService favoriteService;

    @GetMapping("/me")
    public ResponseEntity<List<FavoriteRoomResponse>> getMyFavorites(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<FavoriteRoomResponse> favoriteRoomResponses = favoriteService.getMyFavoriteRooms(userDetails.getMemberId());
        return ResponseEntity.ok(favoriteRoomResponses);
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<Void> addFavorite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long roomId) {
        favoriteService.addFavorite(userDetails.getMemberId(), roomId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> removeFavorite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long roomId) {
        favoriteService.removeFavorite(userDetails.getMemberId(), roomId);
        return ResponseEntity.noContent().build();
    }
}
