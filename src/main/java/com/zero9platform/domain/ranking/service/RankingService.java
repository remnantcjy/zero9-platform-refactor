package com.zero9platform.domain.ranking.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.ProgressStatus;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.domain.ranking.model.response.*;
import com.zero9platform.domain.ranking.policy.RankingPeriodPolicy;
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
    private final Cache<String, Object> rankingCache;

    /**
     * 공동구매 게시물 랭킹 (조회수 기준)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostRankingListResponse> groupPurchasePostRanking(RankingPeriod period) {

        // 기본값
        if (period == null) {period = RankingPeriod.REALTIME;}

        LocalDateTime[] range = RankingPeriodPolicy.resolve(period, null, null);

        // 캐시 키
        String cacheKey = "RANKING:GPP:" + period.name();

        // 캐시조회
        @SuppressWarnings("unchecked")
        List<GroupPurchasePostRankingListResponse> cached = (List<GroupPurchasePostRankingListResponse>) rankingCache.getIfPresent(cacheKey);
        if (cached != null && !cached.isEmpty()) {return cached;}

        // 조회수 기준 상위 10개 게시물 조회
        List<GroupPurchasePost> posts = groupPurchasePostRepository.findTopByViewCountBetween(range[0], range[1], ProgressStatus.DOING.name(), PageRequest.of(0, 10));

        // 랭킹 순위를 응답 시점에 부여하기 위한 카운터
        AtomicInteger rank = new AtomicInteger(1);

        // 집계 결과를 랭킹 응답 DTO로 변환
        List<GroupPurchasePostRankingListResponse> responses =
                posts.stream()
                .map(p -> GroupPurchasePostRankingListResponse.from(
                                rank.getAndIncrement(),
                                p.getId(),
                                p.getProductName(),
                                p.getViewCount()
                        ))
                .toList();

        // 조회 결과를 캐시에 저장
        rankingCache.put(cacheKey, responses);
        return responses;
    }

    /**
     * 인기 검색어 랭킹 (키워드)
     */
    @Transactional(readOnly = true)
    public List<SearchLogRankingListResponse> searchLogKeywordRanking(RankingPeriod period) {

        // 기본값
        if (period == null) {period = RankingPeriod.REALTIME;}

        // period 공백 검증
        LocalDateTime[] range = RankingPeriodPolicy.resolve(period, null, null);

        // 캐시 키
        String cacheKey = "RANKING:SEARCH:" + period.name();

        // 캐시 조회
        @SuppressWarnings("unchecked")
        List<SearchLogRankingListResponse> cached = (List<SearchLogRankingListResponse>) rankingCache.getIfPresent(cacheKey);
        if (cached != null && !cached.isEmpty()) {return cached;}

        // 검색 횟수 기준 상위 10개 키워드 조회
        List<SearchLogRankingAggregateResponse> searchLogs = searchLogRepository.findTopKeywordsBetween(range[0], range[1], PageRequest.of(0, 10));

        AtomicInteger rank = new AtomicInteger(1);
        List<SearchLogRankingListResponse> result =
                searchLogs.stream()
                .map(l -> SearchLogRankingListResponse.from(
                                rank.getAndIncrement(),
                                l.getKeyword(),
                                l.getCount()
                        ))
                .toList();

        // 조회 결과를 캐시에 저장
        rankingCache.put(cacheKey, result);
        return result;
    }

    /**
     * 실시간 공동구매 상품 게시물 랭킹(찜)
     */
    @Transactional(readOnly = true)
    public List<ProductPostFavoriteRankingListResponse> loadRealtimeFavoriteRanking(RankingPeriod period) {

        // 기본값
        if (period == null) {period = RankingPeriod.REALTIME;}

        // period 공백 검증
        LocalDateTime[] range = RankingPeriodPolicy.resolve(period, null, null);

        // 캐시 키
        String cacheKey = "RANKING:PRODUCT:FAVORITE:" + period.name();

        // 캐시 조회
        @SuppressWarnings("unchecked")
        List<ProductPostFavoriteRankingListResponse> cached = (List<ProductPostFavoriteRankingListResponse>) rankingCache.getIfPresent(cacheKey);
        if (cached != null && !cached.isEmpty()) return cached;

        // 캐시 miss 시 DB에서 실시간 집계 조회
        List<ProductPostFavoriteRankingAggregateResponse> favorite = productPostFavoriteRepository.findTopByFavoriteBetween(range[0], range[1], ProgressStatus.DOING.name(), PageRequest.of(0, 10));

        // 랭킹 순위를 응답 시점에 부여하기 위한 카운터
        AtomicInteger rank = new AtomicInteger(1);
        List<ProductPostFavoriteRankingListResponse> result =
                favorite.stream()
                        .map(a -> ProductPostFavoriteRankingListResponse.from(
                                        rank.getAndIncrement(),
                                        a.getProductPostId(),
                                        a.getTitle(),
                                        a.getFavoriteCount()
                                ))
                        .toList();

        // 조회 결과를 캐시에 저장
        rankingCache.put(cacheKey, result);
        return result;
    }

    /**
     * 기간별 상품 찜 랭킹 (관리자용)
     * period: DAILY / WEEKLY / MONTHLY
     */
    @Transactional(readOnly = true)
    public List<ProductPostFavoriteRankingListResponse> loadPeriodFavoriteRanking(AuthUser authUser, RankingPeriod period, LocalDate from, LocalDate to) {

        // 관리자 권한 검증
        if (!"ADMIN".equals(authUser.getUserRole().name())) {throw new CustomException(ExceptionCode.NO_PERMISSION);}

        // period 공백 검증
        if (period == null || period.name().isBlank()) {period = RankingPeriod.REALTIME;}

        // 기간 랭킹 허용 값 검증
        if (period != RankingPeriod.REALTIME && period != RankingPeriod.DAILY && period != RankingPeriod.WEEKLY && period != RankingPeriod.MONTHLY) {throw new CustomException(ExceptionCode.INVALID_PERIOD);}

        // 날짜 검증
        if (from != null && to != null && from.isAfter(to)) {throw new CustomException(ExceptionCode.INVALID_DATE_RANGE);}

        // period 공백 검증
        LocalDateTime[] range = RankingPeriodPolicy.resolve(period, from, to);

        // 기간 내 찜 기준 랭킹 집계
        List<ProductPostFavoriteRankingAggregateResponse> favorite = productPostFavoriteRepository.findTopByFavoriteBetween(range[0], range[1], ProgressStatus.DOING.name(), PageRequest.of(0, 10));

        AtomicInteger rank = new AtomicInteger(1);
        List<ProductPostFavoriteRankingListResponse> result = favorite.stream()
                .map(a ->
                        ProductPostFavoriteRankingListResponse.from(
                                rank.getAndIncrement(),
                                a.getProductPostId(),
                                a.getTitle(),
                                a.getFavoriteCount()
                        ))
                .toList();

        return result;
    }
}
