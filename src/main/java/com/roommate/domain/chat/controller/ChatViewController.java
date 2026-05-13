package com.roommate.domain.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatViewController {

    @GetMapping("/chat/rooms")
    public String chatRooms() {
        return "chat/list";
    }

    @GetMapping("/chat/rooms/{chatRoomId}")
    public String chatRoom(@PathVariable Long chatRoomId, Model model) {
        model.addAttribute("chatRoomId", chatRoomId);
        return "chat/room";
    }
}
