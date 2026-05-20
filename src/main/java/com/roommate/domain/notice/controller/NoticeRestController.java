package com.roommate.domain.notice.controller;

import com.roommate.domain.notice.dto.NoticeListResponse;
import com.roommate.domain.notice.dto.NoticeResponse;
import com.roommate.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeRestController {
    private final NoticeService noticeService;

    @GetMapping
    public NoticeListResponse getNotices(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String keyword) {
        return noticeService.getPublicNotices(page, size, keyword);
    }

    @GetMapping("/{noticeId}")
    public NoticeResponse getNotice(@PathVariable Long noticeId) {
        return noticeService.getPublicNotice(noticeId);
    }
}
