package com.zero9platform.domain.ranking.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.PppProgressStatus;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.ranking.controller.RankingPeriod;
import com.zero9platform.domain.ranking.model.response.*;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final SearchLogRepository searchLogRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final Cache<String, List<ProductPostFavoriteRankingListResponse>> caffeineCache;

    private static final String FAVORITE_RANKING_KEY = "RANKING:PRODUCT:FAVORITE:TOP10";

    /**
     * 공동구매 게시물 랭킹 (조회수 기준)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostRankingListResponse> groupPurchasePostRanking() {

        // 조회수 기준 상위 10개 게시물 조회
        List<GroupPurchasePost> posts = groupPurchasePostRepository.findTop10ByDeletedAtIsNullOrderByViewCountDesc();

        AtomicInteger rank = new AtomicInteger(1);

        // 집계 결과를 랭킹 응답 DTO로 변환
        return posts.stream()
                .map(aggregate ->
                        GroupPurchasePostRankingListResponse.from(
                                rank.getAndIncrement(),
                                aggregate.getId(),
                                aggregate.getProductName(),
                                aggregate.getViewCount()
                        )
                )
                .toList();
    }

    /**
     * 인기 검색어 랭킹 (키워드 조회수)
     */
    @Transactional(readOnly = true)
    public List<SearchLogRankingListResponse> searchLogKeywordRanking() {

        // 검색 횟수 기준 상위 10개 키워드 조회
        List<SearchLogRankingAggregateResponse> searchLogs = searchLogRepository
                .findTop10Keywords(PageRequest.of(0, 10));

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


    /* =============================
     * Realtime Ranking (Main)
     * ============================= */

    /**
     * 실시간 상품 찜 랭킹
     * - Cache-Aside 전략 사용
     */
    private List<ProductPostFavoriteRankingListResponse> loadRealtimeFavoriteRanking(RankingPeriod rankingPeriod) {

        // Cache-Aside: 캐시에서 먼저 조회
        List<ProductPostFavoriteRankingListResponse> cache = caffeineCache.getIfPresent(FAVORITE_RANKING_KEY);
        if (cache != null) {
            return cache;
        }

        // 캐시 miss 시 DB에서 실시간 집계 조회
        List<ProductPostFavoriteRankingAggregateResponse> favorite = productPostFavoriteRepository
                .findTop10ProductPostByFavorite(PppProgressStatus.DOING.name(), PageRequest.of(0, 10)
                );

        // 랭킹 순위를 응답 시점에 부여하기 위한 카운터
        AtomicInteger rank = new AtomicInteger(1);
        List<ProductPostFavoriteRankingListResponse> responses =
                favorite.stream()
                        .map(aggregate ->
                                ProductPostFavoriteRankingListResponse.from(
                                        rank.getAndIncrement(),
                                        aggregate.getProductPostId(),
                                        aggregate.getTitle(),
                                        aggregate.getFavoriteCount()
                        )
                )
                .toList();

        // 조회 결과를 캐시에 저장
        caffeineCache.put(FAVORITE_RANKING_KEY, responses);
        return responses;
    }

    /**
     * 기간별 랭킹 (회고 / 비상용)
     * period: DAILY / WEEKLY / MONTHLY
     */
    @Transactional(readOnly = true)
    public List<ProductPostFavoriteRankingListResponse> loadPeriodFavoriteRanking(RankingPeriod period,
                                                                                  LocalDate from,
                                                                                  LocalDate to) {



        // REALTIME은 캐시 기반 메인 랭킹
        if(period == null || period == RankingPeriod.REALTIME){
            return loadRealtimeFavoriteRanking(period);
        }

        // 기간 랭킹 허용 값 검증
        if (period != RankingPeriod.DAILY && period != RankingPeriod.WEEKLY && period != RankingPeriod.MONTHLY) {
            throw new CustomException(ExceptionCode.INVALID_PERIOD);
        }

        // 기간 계산(단일 책임)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end;

        // 관리자 커스텀 기간 우선
        if (from != null && to != null) {
            start = from.atStartOfDay();
            end   = to.atTime(23, 59, 59);
            throw new CustomException(ExceptionCode.INVALID_DATE_RANGE);
        } else {
            switch (period) {
                case DAILY -> {
                    start = now.minusDays(1).toLocalDate().atStartOfDay();
                    end = now;
                }
                case WEEKLY -> {
                    start = now.minusWeeks(1);
                    end = now;
                }
                case MONTHLY -> {
                    start = now.minusMonths(1);
                    end = now;
                }
                default -> throw new IllegalArgumentException(
                        "기간 랭킹은 관리자/회고 전용입니다."
                );
            }
        }

        // 기간 내 찜 기준 랭킹 집계
        List<ProductPostFavoriteRankingAggregateResponse> favorite =
                productPostFavoriteRepository.findTopByFavoriteBetween(start, end, PppProgressStatus.DOING.name(), PageRequest.of(0, 10)
                );

        AtomicInteger rank = new AtomicInteger(1);
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
}
