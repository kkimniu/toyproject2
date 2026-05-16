package com.roommate.admin.controller;

import com.roommate.admin.dto.AdminMemberListResponse;
import com.roommate.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/members")
    public AdminMemberListResponse getMembers(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return adminMemberService.getMembers(page, size);
    }
}
