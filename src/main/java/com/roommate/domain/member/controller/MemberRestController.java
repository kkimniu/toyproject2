package com.roommate.domain.member.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.member.dto.request.MemberPasswordChangeRequest;
import com.roommate.domain.member.dto.request.MemberProfileUpdateRequest;
import com.roommate.domain.member.dto.response.FormCodesResponse;
import com.roommate.domain.member.dto.response.MemberResponse;
import com.roommate.domain.member.dto.response.PasswordChangeResponse;
import com.roommate.domain.member.dto.response.WorkTypeResponse;
import com.roommate.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<List<WorkTypeResponse>> getAllWorkTypes() {
        List<WorkTypeResponse> workTypes = memberService.getFormCodes().getWorkTypes();
        return ResponseEntity.ok(workTypes);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        MemberResponse memberResponse = memberService.memberInfo(userDetails.getMemberId());
        return ResponseEntity.ok(memberResponse);
    }

    /**
     * 회원가입 / 프로필 수정 화면에서 사용하는
     * 공통 코드(직업, 취미, 생활 선호, 반려동물)를 한 번에 조회합니다.
     */
    @GetMapping("/form-codes")
    public ResponseEntity<FormCodesResponse> getFormCodes() {
        return ResponseEntity.ok(memberService.getFormCodes());
    }

    /**
     * 내 프로필 수정
     */
    @PutMapping("/me")
    public ResponseEntity<MemberResponse> updateMemberProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody MemberProfileUpdateRequest MemberProfileUpdateRequest) {
        MemberResponse updated = memberService.updateMemberProfile(userDetails.getMemberId(), MemberProfileUpdateRequest);
        // 200 OK 로 반환해도 되지만, Location을 주고 싶으면 created 형태도 가능.
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/me/photo")
    public ResponseEntity<MemberResponse> updateProfilePhoto(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("file") MultipartFile multipartFile) {
        MemberResponse updated = memberService.updateMemberProfilePhoto(userDetails.getMemberId(), multipartFile);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<PasswordChangeResponse> changeMyPassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody MemberPasswordChangeRequest memberPasswordChangeRequest) {
        memberService.changeMyPassword(userDetails.getMemberId(), memberPasswordChangeRequest);
        return ResponseEntity.ok(new PasswordChangeResponse("비밀번호가 성공적으로 변경되었습니다."));
    }
}
