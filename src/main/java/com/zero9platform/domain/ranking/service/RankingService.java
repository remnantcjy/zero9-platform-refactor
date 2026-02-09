package com.zero9platform.domain.ranking.service;

import com.zero9platform.common.enums.ProgressStatus;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.ranking.model.response.*;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final SearchLogRepository searchLogRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final StringRedisTemplate redisTemplate;

    // gpp 랭킹용 상수값 선언
    private static final String GPP_TOTAL_RANKING_KEY = "gpp:ranking:total";
    private static final String GPP_DAILY_RANKING_KEY_PREFIX = "gpp:ranking:daily:";
    private static final int RANKING_LIMIT = 10;
    private static final String GPP_WEEKLY_RANKING_KEY_PREFIX = "gpp:ranking:weekly:";
    private static final int WEEK_DAYS = 7;
    
    /**
     * 공동구매 게시물 랭킹 (조회수 기준)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostRankingListResponse> groupPurchasePostRanking() {

        // 조회수 기준 상위 10개 공동구매 게시물 조회
        List<GroupPurchasePost> posts = groupPurchasePostRepository.findTop10ByDeletedAtIsNullOrderByViewCountDesc();

        AtomicInteger rank = new AtomicInteger(1);

        // 집계 결과를 랭킹 응답 DTO로 변환
        return posts.stream()
                .map(aggregate -> GroupPurchasePostRankingListResponse.from(
                                rank.getAndIncrement(),
                                aggregate.getId(),
                                aggregate.getProductName(),
                                aggregate.getViewCount()
                        )
                )
                .toList();
    }

    /**
     * 공동구매 게시물 전체 누적 랭킹 (Redis)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostTotalRankingResponse> groupPurchasePostTotalRanking() {

        // Redis ZSet에서 상위 N개 조회
        Set<ZSetOperations.TypedTuple<String>> rankingSet =
                redisTemplate.opsForZSet().reverseRangeWithScores(GPP_TOTAL_RANKING_KEY, 0, RANKING_LIMIT - 1);

        // Redis 데이터가 없으면 빈 리스트 반환
        if (rankingSet == null || rankingSet.isEmpty()) {
            return List.of();
        }

        // Redis에 있는 게시물 ID만 추출
        List<Long> gppIds = rankingSet.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();

        // 삭제되지 않은 게시물만 조회
        List<GroupPurchasePost> gppLists =
                groupPurchasePostRepository.findAllByIdInAndDeletedAtIsNull(gppIds);

        // ID -> Entity 매핑 (O(1) 조회용)
        Map<Long, GroupPurchasePost> gppMap =
                gppLists.stream()
                        .collect(Collectors.toMap(GroupPurchasePost::getId, p -> p));

        // Redis 순서 유지하면서 DTO 생성
        List<GroupPurchasePostTotalRankingResponse> result = new ArrayList<>();

        // 랭킹 카운터용
        // 메서드 내부에서 멀티스레드처리(카운터 공유) 안함, atomicInteger 안써도 된다고 판단
        int rank = 1;

        // Redis ZSet에서 가져온 데이터를 하나씩 꺼내는 반복문
        // tuple 내부에 [ value:gppId(String), score:조회수(Double) ]
        for (ZSetOperations.TypedTuple<String> tuple : rankingSet) {

            // null 방어 코드 - null이면 건너뜀
            if (tuple.getValue() == null || tuple.getScore() == null) {
                continue;
            }

            // Redis ZSet의 튜플안에 String으로 저장했기 때문에 Long으로 변환
            Long gppId = Long.valueOf(tuple.getValue());
            // Map에서 id로 엔티티 가져옴
            GroupPurchasePost gpp = gppMap.get(gppId);

            // 삭제된 게시물이거나 DB에 없는 경우 스킵
            if (gpp == null) {
                continue;
            }

            result.add(
                    GroupPurchasePostTotalRankingResponse.from(
                            rank++,
                            gpp.getId(),
                            gpp.getProductName(),
                            tuple.getScore().longValue()
                    )
            );
        }

        return result;
    }

    /**
     * 공동구매 게시물 오늘의 실시간 랭킹 (Redis)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostTodayRankingResponse> groupPurchasePostTodayRanking() {

        // 일일 키값
        String todayKey = GPP_DAILY_RANKING_KEY_PREFIX + LocalDate.now();

        // Redis ZSet 조회
        Set<ZSetOperations.TypedTuple<String>> rankingSet =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(todayKey, 0, RANKING_LIMIT - 1);

        if (rankingSet == null || rankingSet.isEmpty()) {
            return List.of();
        }

        // ID 추출
        List<Long> gppIds = rankingSet.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();

        // 삭제되지 않은 게시물 조회
        List<GroupPurchasePost> gppLists =
                groupPurchasePostRepository.findAllByIdInAndDeletedAtIsNull(gppIds);

        // Map 변환 (O(1) 조회)
        Map<Long, GroupPurchasePost> gppMap =
                gppLists.stream()
                        .collect(Collectors.toMap(GroupPurchasePost::getId, p -> p));

        // DTO 생성
        List<GroupPurchasePostTodayRankingResponse> result = new ArrayList<>();

        // 랭킹 카운터용
        // 메서드 내부에서 멀티스레드처리(카운터 공유) 안함, atomicInteger 안써도 된다고 판단
        int rank = 1;

        // Redis ZSet에서 가져온 데이터를 하나씩 꺼내는 반복문
        // tuple 내부에 [ value:gppId(String), score:조회수(Double) ]
        for (ZSetOperations.TypedTuple<String> tuple : rankingSet) {

            // null 방어 코드 - null이면 건너뜀
            if (tuple.getValue() == null || tuple.getScore() == null) {
                continue;
            }

            // Redis ZSet의 튜플안에 String으로 저장했기 때문에 Long으로 변환
            Long gppId = Long.valueOf(tuple.getValue());
            // Map에서 id로 엔티티 가져옴
            GroupPurchasePost gpp = gppMap.get(gppId);

            // 삭제된 게시물이거나 DB에 없는 경우 스킵
            if (gpp == null) {
                continue;
            }

            result.add(
                    GroupPurchasePostTodayRankingResponse.from(
                            rank++,
                            gpp.getId(),
                            gpp.getProductName(),
                            tuple.getScore().longValue()
                    )
            );
        }

        return result;
    }

    /**
     * 공동구매 게시물 주간 랭킹 TOP10 (ZUNIONSTORE 기반)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostTodayRankingResponse> groupPurchasePostWeeklyRanking() {

        // 최근 7일 key 생성
        List<String> last7DaysKeys = new ArrayList<>();

        for (int i = 0; i < WEEK_DAYS; i++) {
            String key = GPP_DAILY_RANKING_KEY_PREFIX + LocalDate.now().minusDays(i);
            last7DaysKeys.add(key);
        }

        String weeklyKey = GPP_WEEKLY_RANKING_KEY_PREFIX + LocalDate.now();

        // ZUNIONSTORE 실행
        // 최근 7일 daily ZSet을 합산하여 weeklyKey에 저장
        redisTemplate.opsForZSet().unionAndStore(
                last7DaysKeys.get(0),
                last7DaysKeys.subList(1, last7DaysKeys.size()),
                weeklyKey
        );

        // 상위 10개 조회
        Set<ZSetOperations.TypedTuple<String>> rankingSet =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(weeklyKey, 0, RANKING_LIMIT - 1);

        if (rankingSet == null || rankingSet.isEmpty()) {
            return List.of();
        }

        List<Long> gppIds = rankingSet.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();

        List<GroupPurchasePost> gppLists = groupPurchasePostRepository.findAllByIdInAndDeletedAtIsNull(gppIds);

        Map<Long, GroupPurchasePost> gppMap =
                gppLists.stream()
                        .collect(Collectors.toMap(GroupPurchasePost::getId, p -> p));

        List<GroupPurchasePostTodayRankingResponse> result = new ArrayList<>();

        int rank = 1;

        for (ZSetOperations.TypedTuple<String> tuple : rankingSet) {

            if (tuple.getValue() == null || tuple.getScore() == null) continue;

            Long gppId = Long.valueOf(tuple.getValue());
            GroupPurchasePost post = gppMap.get(gppId);

            if (post == null) continue;

            result.add(
                    GroupPurchasePostTodayRankingResponse.from(
                            rank++,
                            post.getId(),
                            post.getProductName(),
                            tuple.getScore().longValue()
                    )
            );
        }

        return result;
    }

    /**
     * 상품판매 게시물 랭킹 (찜 기준)
     */
    @Transactional(readOnly = true)
    public List<ProductPostFavoriteRankingListResponse> productPostFavoriteRanking() {

        // 찜 개수 기준 상위 10개 상품 게시물 집계 조회
        List<ProductPostFavoriteRankingAggregateResponse> favorite = productPostFavoriteRepository
                .findTop10ProductPostByFavorite(ProgressStatus.DOING, PageRequest.of(0, 10));

        AtomicInteger rank = new AtomicInteger(1);

        // 집계 결과를 랭킹 응답 DTO로 변환
        return favorite.stream()
                .map(aggregate ->
                        ProductPostFavoriteRankingListResponse.from(
                                rank.getAndIncrement(),
                                aggregate.getProductPostId(),
                                aggregate.getTitle(),
                                aggregate.getFavoriteCount()
                        )
                )
                .toList();
    }

    /**
     * 인기 검색어 랭킹 (키워드 조회수)
     * 고도화 이후 클릭로그집계로 변경
     */
    @Transactional(readOnly = true)
    public List<SearchLogRankingListResponse> searchLogKeywordRanking() {

        // 검색 횟수 기준 상위 10개 키워드 조회
        List<SearchLogRankingAggregateResponse> searchLogs = searchLogRepository
                .findTopKeywords(PageRequest.of(0, 10));

        AtomicInteger rank = new AtomicInteger(1);

        // 검색어 집계 결과를 랭킹 응답 DTO로 변환
        return searchLogs.stream()
                .map(aggregate ->
                        SearchLogRankingListResponse.from(
                                rank.getAndIncrement(),
                                aggregate.getKeyword(),
                                aggregate.getCount()
                        )
                )
                .toList();
    }
}
