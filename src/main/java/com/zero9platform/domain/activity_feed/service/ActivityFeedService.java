package com.zero9platform.domain.activity_feed.service;

import com.zero9platform.common.enums.FeedType;
import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.repository.ActivityFeedRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
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
    private final ProductPostFavoriteRepository favoriteRepository;

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
     * 피드 전체목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ActivityFeedResponse> feedsGetList(Pageable pageable) {
        // 조회
        Page<ActivityFeed> page = feedRepository.findAll(pageable);

        // DTO 변환
        return  page.map(ActivityFeedResponse::from);
    }

    /**
     * 로그인한 사용자의 피드 전체목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ActivityFeedResponse> myFeedsGetList(Long userId, Pageable pageable) {

        // 유저가 찜한 상품 리스트 가져오기
        List<Long> favoriteList = favoriteRepository.findProductPostIdsByUserId(userId);

        // 찜한 상품이 없다면 전체피드 조회
        if (favoriteList.isEmpty()) {
            return feedsGetList(pageable);
        }

        // 찜한 상품 + 전체 피드 합쳐서 조회
        return feedRepository.findFeedsByInterest(favoriteList, pageable)
                .map(ActivityFeedResponse::from);
    }

}
