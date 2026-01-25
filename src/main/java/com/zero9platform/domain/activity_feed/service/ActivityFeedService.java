package com.zero9platform.domain.activity_feed.service;

import com.zero9platform.common.enums.FeedType;
import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.repository.ActivityFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityFeedService {

    private final ActivityFeedRepository feedRepository;

    @Transactional
    public void feedCreate(String type, Long productPostId, String productName) {

        // 중복 방지(이미 있으면 패스)
        if (feedRepository.existsByTypeAndProductPostId(type, productPostId)) return;

        String message = "";
        if (type.equals(FeedType.POPULAR.name())) {
            message = "[" + productName + "] 상품이 많은 관심을 받고 있습니다.";
        } else if (type.equals(FeedType.PAYMENT.name())) {
            message = "[" + productName + "] 상품의 새로운 주문이 접수되었습니다.";
        } else if (type.equals(FeedType.DEADLINE.name())) {
            message = "[" + productName + "] 상품의 모집 마감이 임박하였습니다.";
        } else if (type.equals(FeedType.LOW_STOCK.name())) {
            message = "[" + productName + "] 상품의 재고가 소진될 예정입니다.";
        } else if (type.equals(FeedType.SOON.name())) {
            message = "[" + productName + "] 상품이 곧 공개될 예정입니다.";
        }

        // 유저 생성할 때처럼 아무 변환 없이 그대로 생성자에 전달
        ActivityFeed feed = new ActivityFeed(type, message, productPostId);
        feedRepository.save(feed);
    }

    @Transactional(readOnly = true)
    public List<ActivityFeedResponse> feedsGetList() {
        // 조회
        List<ActivityFeed> feeds = feedRepository.findAll();

        // 빈 리스트 생성
        List<ActivityFeedResponse> responses = new ArrayList<>();

        // 향상된 for문으로 하나씩 DTO 변환 후 추가
        for (ActivityFeed feed : feeds) {
            responses.add(ActivityFeedResponse.from(feed));
        }

        return responses;
    }
}
