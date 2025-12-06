package com.roommate.domain.room.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.room.dto.response.RoomDetailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.roommate.domain.room.service.RoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("rooms")
@RequiredArgsConstructor
public class RoomViewController {

    @Value("${kakao.js.key}")
    private String kakaoJsKey;

    private final RoomService roomService;

    @GetMapping("")
    public String roomMainPage(Model model) {

        model.addAttribute("kakaoJsKey",kakaoJsKey);
        return "rooms/main";
    }

    /**
     * 지도에서 찾기 페이지
     *
     * 왜 이렇게 했는지:
     * - 이 컨트롤러는 "화면(View)"만 반환하고
     *   실제 데이터는 JS가 REST API(/api/rooms/**)로 가져가도록 분리.
     * - 그래서 Model에 데이터를 따로 넣을 필요가 없음.
     */
    @GetMapping("/map")
    public String showRoomMapPage(Model model) {
        // /WEB-INF/views/room/map.jsp
        model.addAttribute("kakaoJsKey",kakaoJsKey);
        return "rooms/map";
    }

    /**
     * 방 상세 페이지 (추후 구현용)
     *
     */
    @GetMapping("/{roomId}")
    public String roomDetailView(@PathVariable Long roomId, Model model) {
        // View에서는 로그인/작성자 정보 전혀 안 씀
        model.addAttribute("roomId", roomId);
        model.addAttribute("kakaoJsKey", kakaoJsKey);
        return "rooms/detail"; // /WEB-INF/views/rooms/detail.jsp
    }
}
