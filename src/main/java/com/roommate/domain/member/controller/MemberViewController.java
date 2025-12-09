package com.roommate.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("members")
@RequiredArgsConstructor
public class MemberViewController {

    @GetMapping("/mypage")
    public String mypage() {
        // /WEB-INF/views/members/mypage.jsp 를 의미
        return "members/mypage";
    }
}
