package com.zero9platform.domain.activity_feed.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.service.ActivityFeedService;
import com.zero9platform.domain.auth.model.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/zero9/feeds")
@RequiredArgsConstructor
public class ActivityFeedController {

    private final ActivityFeedService feedService;

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<ActivityFeedResponse>>> feedsGetListHandler(@AuthenticationPrincipal AuthUser authUser, @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        PageResponse<ActivityFeedResponse> response;

        // 로그인 여부에 따른 서비스 로직
        if (authUser == null) {
            // 비로그인: 전체 목록 조회
            response = PageResponse.from(feedService.feedsGetList(pageable));
        } else {
            // 로그인: 찜 등 개인화 목록 조회
            response = PageResponse.from(feedService.myFeedsGetList(authUser.getId(), pageable));
        }

        return ResponseEntity.ok(CommonResponse.success("피드 목록 조회 성공", response));
    }
}

