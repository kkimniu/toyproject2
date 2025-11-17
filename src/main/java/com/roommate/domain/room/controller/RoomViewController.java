package com.roommate.domain.room.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.roommate.domain.room.service.RoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("room")
@RequiredArgsConstructor
public class RoomViewController {

    // application.properties 의 kakao.map.api.key 값을 주입
    @Value("${kakao.map.api.key}")
    private String kakaoMapApiKey;
    //private final RoomService roomService;

    @GetMapping("")
    public String roomMainPage(Model model) {

        model.addAttribute("kakaoMapKey",kakaoMapApiKey);
        return "room/main";
    }
}
