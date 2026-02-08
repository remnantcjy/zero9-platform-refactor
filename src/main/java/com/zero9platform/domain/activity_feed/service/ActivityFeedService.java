package com.zero9platform.domain.activity_feed.service;

import com.zero9platform.common.enums.FeedType;
import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.repository.ActivityFeedRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityFeedService {

    private final ActivityFeedRepository feedRepository;
    private final ProductPostFavoriteRepository favoriteRepository;

    /**
     * 통합 피드 처리 로직 (Upsert)
     */
    @Transactional
    public void feedUpsert(FeedType type, Long targetId, String title, Long userId, Object... args) {

        // 집계형(isAggregation=true)인 경우 기존 피드 존재 여부 확인
        if (type.isAggregation()) {
            Optional<ActivityFeed> existingFeed = feedRepository.findFirstByTypeAndTargetIdAndUserIdOrderByCreatedAtDesc(type.name(), targetId, userId);

            if (existingFeed.isPresent()) {
                ActivityFeed feed = existingFeed.get();
                // 메시지 업데이트 시 combineArgs가 title과 args(숫자 등)를 합쳐줌
                feed.updateMessage(type.toMessage(combineArgs(title, args)));
                return;
            }
        }

        // 집계형이 아니거나 기존 데이터가 없으면 신규 생성
        String message = type.toMessage(combineArgs(title, args));
        ActivityFeed feed = new ActivityFeed(type.name(), message, targetId, userId);
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
     * 로그인한 사용자의 피드(현재는 찜) 조회
     */
    @Transactional(readOnly = true)
    public Page<ActivityFeedResponse> feedsGetMyList(Long userId, Pageable pageable) {

        // 유저가 찜한 상품 리스트 가져오기
        List<Long> favoriteList = favoriteRepository.findProductPostIdsByUserId(userId);

        if (favoriteList.isEmpty()) {
            return feedRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                    .map(ActivityFeedResponse::from);
        }

        // 찜관련 피드 + 개인 알림 합치기
        return feedRepository.findFeedsByFavoriteListOrUserId(favoriteList, userId, pageable)
                .map(ActivityFeedResponse::from);
    }

    /**
     * 타이틀과 가변 인자를 하나의 배열로 합치는 헬퍼 메소드
     */
    private Object[] combineArgs(String title, Object... args) {
        if (args == null || args.length == 0) {
            return new Object[]{title};
        }
        Object[] combined = new Object[args.length + 1];
        combined[0] = title;
        System.arraycopy(args, 0, combined, 1, args.length);
        return combined;
    }
}
