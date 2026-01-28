package com.zero9platform.domain.activity_feed.service;

import com.zero9platform.common.enums.FeedType;
import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.repository.ActivityFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityFeedService {

    private final ActivityFeedRepository feedRepository;

    /**
     * 피드 생성
     */
    @Transactional
    public void feedCreate(FeedType type, Long targetId, String title) {

        // 중복 방지(이미 있으면 패스)
        if (targetId != null && feedRepository.existsByTypeAndTargetId(type.name(), targetId)) {
            return;
        }

        // enum 내부 메소드호출 - 피드 내용(메시지) 위임
        String message = type.toMessage(title);

        // 유저id 필드 null이면 전체피드 (현재 개인별 X)
        ActivityFeed feed = new ActivityFeed(type.name(), message, targetId, null);
        feedRepository.save(feed);
    }

    /**
     * 피드 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ActivityFeedResponse> feedsGetList(Pageable pageable) {
        // 조회
        Page<ActivityFeed> page = feedRepository.findAll(pageable);

        // DTO 변환
        return  page.map(ActivityFeedResponse::from);
    }
}
