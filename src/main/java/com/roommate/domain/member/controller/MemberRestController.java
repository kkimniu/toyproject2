package com.roommate.domain.member.controller;

import com.roommate.domain.member.entity.WorkTypeEntity;
import com.roommate.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/members")
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    /**
     * 일 종류에 전체를 조회합니다
     */
    @GetMapping("/work-types")
    public ResponseEntity<List<WorkTypeEntity>> getAllWorkTypes(){
        List<WorkTypeEntity> workTypes = memberService.findAllWorkType();
        return ResponseEntity.ok(workTypes);
    }


}
