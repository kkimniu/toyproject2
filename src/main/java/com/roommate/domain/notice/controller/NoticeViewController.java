package com.roommate.domain.notice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/notices")
public class NoticeViewController {
    @GetMapping
    public String notices() {
        return "notices/list";
    }

    @GetMapping("/{noticeId}")
    public String noticeDetail(@PathVariable Long noticeId, Model model) {
        model.addAttribute("noticeId", noticeId);
        return "notices/detail";
    }
}
