package com.roommate.domain.member.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.member.dto.response.FormCodesResponse;
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
        List<WorkTypeResponse> workTypes = memberService.getFormCodes().getWorkTypes();
        return ResponseEntity.ok(workTypes);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        MemberResponse memberResponse = memberService.memberInfo(userDetails.getMemberId());
        return ResponseEntity.ok(memberResponse);
    }

    /**
     * 회원가입 / 프로필 수정 화면에서 사용하는
     * 공통 코드(직업, 취미, 생활 선호, 반려동물)를 한 번에 조회합니다.
     *
     */
    @GetMapping("/form-codes")
    public ResponseEntity<FormCodesResponse> getFormCodes() {
        return ResponseEntity.ok(memberService.getFormCodes());
    }
}
