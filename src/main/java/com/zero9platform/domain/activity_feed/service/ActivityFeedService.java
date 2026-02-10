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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityFeedService {

    private final ActivityFeedRepository feedRepository;
    private final ProductPostFavoriteRepository favoriteRepository;
    private final ActivityFeedRedisService redisService; // Redis 캐시 및 카운팅 전담 서비스

    /**
     * [생성 로직] 피드 생성 및 Redis 상태 관리
     * - DB에는 변하지 않는 '뼈대(타입, 대상ID)'만 저장합니다.
     * - 실시간 카운트와 리스트 캐시를 Redis에서 즉시 처리하여 Write 성능을 높입니다.
     */
    @Transactional
    public void feedCreate(FeedType type, Long targetId, String targetName, Long userId) {
        // 1. DB 적재: 동일 유저에게 동일 타입/대상의 피드가 중복 생성되지 않도록 체크 후 저장
        if (!feedRepository.existsByTypeAndTargetIdAndUserId(type.name(), targetId, userId)) {
            ActivityFeed feed = new ActivityFeed(type.name(), targetName, targetId, userId);
            feedRepository.save(feed);
        }

        // 2. 실시간 카운팅: Redis의 Atomic INCR 연산을 사용하여 동시성 이슈 없이 구매자 수를 집계 (전략 2)
        if (type.isHasCounter()) {
            redisService.incrementOrderCount(targetId);
        }

        // 3. 캐시 무효화(Cache Evict): 데이터가 변했으므로 해당 유저의 기존 캐시를 삭제 (전략 3)
        redisService.deleteFeedCache(String.valueOf(userId));
    }

    /**
     * [조회 로직] 전체 피드 목록 조회
     * - 페이징 처리를 유지하면서 실시간 카운팅 데이터를 결합합니다.
     */
    @Transactional(readOnly = true)
    public Page<ActivityFeedResponse> feedsGetList(Pageable pageable) {
        // 1. Redis 캐시 확인 (전체 공용 키 'all' 사용)
        // 페이지 번호에 따라 캐시 키를 다르게 가져가야 정합성이 맞습니다.
        String cacheKey = "all:" + pageable.getPageNumber();
        List<ActivityFeedResponse> cached = redisService.getCachedFeeds(cacheKey);

        if (cached != null && !cached.isEmpty()) {
            log.info("[전체피드] 캐시 히트! Redis 데이터를 반환합니다.");
            return new org.springframework.data.domain.PageImpl<>(cached, pageable, cached.size());
        }

        // 2. 캐시 없으면 DB 전체 조회
        Page<ActivityFeedResponse> responsePage = feedRepository.findAll(pageable)
                .map(entity -> {
                    FeedType type = FeedType.valueOf(entity.getType());
                    long count = type.isHasCounter() ? redisService.getOrderCount(entity.getTargetId()) : 0L;
                    return ActivityFeedResponse.from(entity, count);
                });

        // 3. Redis에 전체 피드 캐시 저장
        if (!responsePage.isEmpty()) {
            log.info("[전체피드] DB 데이터를 Redis에 캐싱합니다.");
            redisService.saveFeedCache(cacheKey, responsePage.getContent());
        }

        return responsePage;
    }


    /**
     * [조회 로직] 내 피드 목록 조회 (9초 병목 해결의 핵심)
     * - DB는 페이징을 위한 ID값들만 빠르게 긁어옵니다.
     * - 실제 메시지는 조회 시점에 Redis의 실시간 카운트와 결합하여 '동적 조립'합니다.
     */
    @Transactional(readOnly = true)
    public Page<ActivityFeedResponse> feedsGetMyList(Long userId, Pageable pageable) {

        // 캐시에 데이터가 있다면 9초 걸리는 아래 DB 로직을 통째로 스킵합니다.
        List<ActivityFeedResponse> cached = redisService.getCachedFeeds(String.valueOf(userId));
        if (cached != null && !cached.isEmpty()) {
            log.info("Redis에서 데이터를 즉시 반환합니다. userId: {}", userId);
            return new org.springframework.data.domain.PageImpl<>(cached, pageable, cached.size());
        }

        // 1. 유저의 관심(찜) 상품 ID 리스트 확보
        List<Long> favoriteList = favoriteRepository.findProductPostIdsByUserId(userId);

        Page<ActivityFeed> feedPage;
        // 2. DB 조회: 인덱스(user_id, updated_at)를 타고 뼈대 데이터(Page) 확보
        if (favoriteList.isEmpty()) {
            feedPage = feedRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
        } else {
            feedPage = feedRepository.findFeedsByFavoriteListOrUserId(favoriteList, userId, pageable);
        }

        // 3. 실시간 조립(Late Binding):
        Page<ActivityFeedResponse> responsePage = feedPage.map(entity -> {
            FeedType type = FeedType.valueOf(entity.getType());
            long count = type.isHasCounter() ? redisService.getOrderCount(entity.getTargetId()) : 0L;
            return ActivityFeedResponse.from(entity, count);
        });

        // [추가] 4. 다음 조회를 위해 Redis에 캐시 저장
        if (!responsePage.isEmpty()) {
            redisService.saveFeedCache(String.valueOf(userId), responsePage.getContent());
        }

        return responsePage;
    }
}