package com.roommate.domain.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CommunityPostViewController {
    @GetMapping("/community")
    public String list() {
        return "community/list";
    }

    @GetMapping("/community/new")
    public String create() {
        return "community/form";
    }

    @GetMapping("/community/{communityPostId}")
    public String detail(@PathVariable Long communityPostId, Model model) {
        model.addAttribute("communityPostId", communityPostId);
        return "community/detail";
    }

    @GetMapping("/community/{communityPostId}/edit")
    public String edit(@PathVariable Long communityPostId, Model model) {
        model.addAttribute("communityPostId", communityPostId);
        return "community/form";
    }
}
