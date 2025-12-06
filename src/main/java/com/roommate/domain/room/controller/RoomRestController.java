package com.roommate.domain.room.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.room.dto.request.RoomCreateRequest;
import com.roommate.domain.room.dto.request.RoomStatusUpdateRequest;
import com.roommate.domain.room.dto.request.RoomUpdateRequest;
import com.roommate.domain.room.dto.response.RoomDetailResponse;
import com.roommate.domain.room.dto.response.RoomMapItemResponse;
import com.roommate.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomRestController {
    private final RoomService roomService;

    // 지도용 방 목록
    @GetMapping("/map")
    public List<RoomMapItemResponse> getRoomsForMap(@RequestParam double north, @RequestParam double south, @RequestParam double east, @RequestParam double west, @RequestParam int zoom) {
        // 왜: 지도 이동/줌마다 필요한 영역만 서버에서 조회하여 성능 최적화
        return roomService.getRoomsForMap(north, south, east, west, zoom);
    }

    // 방 상세 조회
    @GetMapping("/{roomId}")
    public RoomDetailResponse getRoomDetail(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        return roomService.getRoomDetail(roomId, memberId);
    }

    // 방 등록
    @PostMapping
    public Long createRoom(@RequestBody RoomCreateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMemberId();
        return roomService.createRoom(request, memberId);
    }

    // 방 수정 (작성자만)
    @PutMapping("/{roomId}")
    public void updateRoom(@PathVariable Long roomId, @RequestBody RoomUpdateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMemberId();
        roomService.updateRoom(roomId, request, memberId);
    }

    // 방 삭제 (soft delete)
    @DeleteMapping("/{roomId}")
    public void deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMemberId();
        roomService.deleteRoom(roomId, memberId);
    }

    // 상태 변경
    @PatchMapping("/{roomId}/status")
    public void changeRoomStatus(@PathVariable Long roomId, @RequestBody RoomStatusUpdateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMemberId();
        roomService.changeStatus(roomId, request, memberId);
    }
}
