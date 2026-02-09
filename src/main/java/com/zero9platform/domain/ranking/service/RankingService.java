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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
                .map(tuple -> Long.valueOf(tuple.getValue()))
                .toList();

        // 삭제되지 않은 게시물만 조회
        List<GroupPurchasePost> posts =
                groupPurchasePostRepository.findAllById(gppIds).stream()
                        .filter(g -> g.getDeletedAt() == null)
                        .toList();

        // 순위용 카운터
        AtomicInteger rank = new AtomicInteger(1);

        // Redis 순서 그대로 DTO 변환
        return rankingSet.stream()
                .map(tuple -> {
                    Long gppId = Long.valueOf(tuple.getValue());

                    // Redis ID -> DB 객체 매칭
                    GroupPurchasePost gpp = posts.stream()
                            .filter(p -> p.getId().equals(gppId))
                            .findFirst()
                            .orElse(null);

                    if (gpp == null) return null;

                    // DTO 생성
                    return GroupPurchasePostTotalRankingResponse.from(
                            rank.getAndIncrement(),
                            gpp.getId(),
                            gpp.getProductName(),
                            tuple.getScore().longValue()
                    );
                })
                .filter(dto -> dto != null)
                .toList();
    }

    /**
     * 공동구매 게시물 오늘의 실시간 랭킹 (Redis)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostTodayRankingResponse> groupPurchasePostTodayRanking() {

        // 오늘 날짜 기반 key 생성
        String todayKey = GPP_DAILY_RANKING_KEY_PREFIX + LocalDate.now();

        // 오늘 ZSet에서 상위 N개 조회 (오늘 증가된 조회수 기준)
        Set<ZSetOperations.TypedTuple<String>> rankingSet =
                redisTemplate.opsForZSet().reverseRangeWithScores(todayKey, 0, RANKING_LIMIT - 1);

        // 값이 없다면 빈리스트 반환
        if (rankingSet == null || rankingSet.isEmpty()) {
            return List.of();
        }

        // Redis에 있는 게시물 ID만 추출
        List<Long> gppIds = rankingSet.stream()
                .map(tuple -> Long.valueOf(tuple.getValue()))
                .toList();

        // 삭제되지 않은 게시물만 조회
        List<GroupPurchasePost> posts =
                groupPurchasePostRepository.findAllById(gppIds).stream()
                        .filter(g -> g.getDeletedAt() == null)
                        .toList();

        // 순위용 카운터
        AtomicInteger rank = new AtomicInteger(1);

        // Redis 순서 그대로 DTO 변환
        return rankingSet.stream()
                .map(tuple -> {
                    Long gppId = Long.valueOf(tuple.getValue());

                    // Redis ID -> DB 객체 매칭
                    GroupPurchasePost post = posts.stream()
                            .filter(p -> p.getId().equals(gppId))
                            .findFirst()
                            .orElse(null);

                    if (post == null) return null;

                    // DTO 생성
                    return GroupPurchasePostTodayRankingResponse.from(
                            rank.getAndIncrement(),
                            post.getId(),
                            post.getProductName(),
                            tuple.getScore().longValue()
                    );
                })
                .filter(dto -> dto != null)
                .toList();
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
