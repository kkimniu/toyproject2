package com.roommate.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberViewController {

    @GetMapping("/mypage")
    public String mypageMain() {
        // 프로필 요약 + 탭 + "프로필 수정" 버튼
        return "members/mypage";          // /WEB-INF/views/members/mypage.jsp
    }

    @GetMapping("/mypage/edit")
    public String mypageEdit() {
        // 실제 수정 폼 화면
        return "members/mypageEdit";     // /WEB-INF/views/members/mypageEdit.jsp
    }
}
