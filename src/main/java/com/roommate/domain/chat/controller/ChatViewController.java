package com.roommate.domain.chat.controller;

import com.roommate.common.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatViewController {
    @GetMapping
    public String chatListPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return "chat/chatList";
    }

    @GetMapping("/{chatRoomId}")
    public String chatRoomPage(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("chatRoomId", chatRoomId);
        return "chat/chatRoom";
    }
}
