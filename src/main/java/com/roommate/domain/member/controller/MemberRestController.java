package com.roommate.domain.member.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.member.dto.response.MemberResponse;
import com.roommate.domain.member.dto.response.WorkTypeResponse;
import com.roommate.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    /**
     * 일 종류에 전체를 조회합니다
     */
    @GetMapping("/work-types")
    public ResponseEntity<List<WorkTypeResponse>> getAllWorkTypes(){
        List<WorkTypeResponse> workTypes = memberService.findAllWorkType();
        return ResponseEntity.ok(workTypes);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        MemberResponse memberResponse = memberService.memberInfo(userDetails.getMemberId());
        return ResponseEntity.ok(memberResponse);
    }

}
