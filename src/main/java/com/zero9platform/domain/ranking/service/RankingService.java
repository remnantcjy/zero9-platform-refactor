package com.zero9platform.domain.ranking.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.ranking.model.response.ProductPostFavoriteRankingListResponse;
import com.zero9platform.domain.ranking.model.response.SearchLogRankingListResponse;
import com.zero9platform.domain.ranking.repository.FavoriteRankingSnapshotRepository;
import com.zero9platform.domain.ranking.repository.KeywordRankingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingCounter rankingCounter;
    private final ProductPostRepository productPostRepository;
    private final FavoriteRankingSnapshotRepository favoriteSnapshotRepository;
    private final KeywordRankingSnapshotRepository keywordSnapshotRepository;
//    private final GroupPurchasePostRepository groupPurchasePostRepository;
//    private final SearchLogRepository searchLogRepository;

    /**
     * 인기 검색어 랭킹 조회
     */
    @Transactional(readOnly = true)
    public List<SearchLogRankingListResponse> searchKeywordRanking(RankingPeriod period) {

        RankingPeriod resolved = resolvePeriod(period);
        validatePeriod(resolved);
        AtomicInteger rank = new AtomicInteger(1);

        // REALTIME → 캐시
        if (resolved == RankingPeriod.REALTIME) {
            return rankingCounter.searchCounters(RankingPeriod.REALTIME)
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .map(e -> SearchLogRankingListResponse.of(
                            rank.getAndIncrement(),
                            rankingCounter.extractKeyword(e.getKey()),
                            e.getValue()
                    ))
                    .toList();
        }


        // DAILY / WEEKLY / MONTHLY → DB
        var snapshots = keywordSnapshotRepository.findByPeriodOrderByKeywordCountDesc(resolved, PageRequest.of(0, 10));
        if (snapshots == null || snapshots.isEmpty()) {
            return List.of();
        }
        return snapshots.stream()
                .map(s -> SearchLogRankingListResponse.of(
                        rank.getAndIncrement(),
                        s.getKeyword(),
                        s.getKeywordCount()
                ))
                .toList();
    }

    /**
     * 공동구매 상품 게시물 랭킹 조회(찜)
     */
    @Transactional(readOnly = true)
    public List<ProductPostFavoriteRankingListResponse> favoriteRanking(RankingPeriod period) {

        RankingPeriod resolved = resolvePeriod(period);
        validatePeriod(resolved);
        AtomicInteger rank = new AtomicInteger(1);

        // REALTIME → 캐시
        if (resolved == RankingPeriod.REALTIME) {
            return rankingCounter.favoriteCounters(RankingPeriod.REALTIME)
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .map(e -> {
                        Long productPostId = rankingCounter.extractProductPostId(e.getKey());
                        ProductPost post = productPostRepository.findById(productPostId)
                                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

                        return ProductPostFavoriteRankingListResponse.of(
                                rank.getAndIncrement(),
                                post.getId(),
                                post.getTitle(),
                                e.getValue()
                        );
                    })
                    .toList();
        }

        // DAILY / WEEKLY / MONTHLY → DB
        var snapshots = favoriteSnapshotRepository.findByPeriodOrderByFavoriteCountDesc(resolved, PageRequest.of(0, 10));
        if (snapshots == null || snapshots.isEmpty()) {
            return List.of();
        }
        return snapshots.stream()
                .map(s -> {
                    ProductPost post = productPostRepository.findById(s.getProductPostId())
                            .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

                    return ProductPostFavoriteRankingListResponse.of(
                            rank.getAndIncrement(),
                            post.getId(),
                            post.getTitle(),
                            s.getFavoriteCount()
                    );
                })
                .toList();
    }


    /**
     * period 공통 처리(null일 경우 REALTIME으로 보정)
     */
    private RankingPeriod resolvePeriod(RankingPeriod period) {
        return (period == null) ? RankingPeriod.REALTIME : period;
    }

    /**
     * 랭킹 조회시 허용된 기간만 처리
     */
    private void validatePeriod(RankingPeriod period) {
        if (period != RankingPeriod.REALTIME &&
                period != RankingPeriod.DAILY &&
                period != RankingPeriod.WEEKLY &&
                period != RankingPeriod.MONTHLY) {
            throw new CustomException(ExceptionCode.INVALID_PERIOD);
        }
    }

//    /**
//     * 공동구매 조회수 랭킹
//     */
//    @Transactional(readOnly = true)
//    public List<GroupPurchasePostRankingListResponse> groupPurchasePostRanking(RankingPeriod period) {
//        RankingPeriod resolved = resolvePeriod(period);
//        Map<String, Long> counters = rankingCounter.getAll(resolved);
//        AtomicInteger rank = new AtomicInteger(1);
//
//        return counters.entrySet().stream()
//                .filter(e -> e.getKey().startsWith(viewPrefix(resolved)))
//                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
//                .limit(10)
//                .map(e -> {
//                    Long gppId = extractLastId(e.getKey());
//                    GroupPurchasePost gpp = groupPurchasePostRepository
//                            .findByIdAndDeletedAtIsNull(gppId)
//                            .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));
//
//                    return GroupPurchasePostRankingListResponse.of(
//                            rank.getAndIncrement(),
//                            gpp.getId(),
//                            gpp.getProductName(),
//                            e.getValue()
//                    );
//                })
//                .toList();
//    }
//
//    /**
//     * 기간별 상품 찜 랭킹 (관리자용)
//     * period: DAILY / WEEKLY / MONTHLY
//     */
//    @Transactional(readOnly = true)
//    public List<ProductPostFavoriteRankingListResponse> loadPeriodFavoriteRanking(AuthUser authUser, RankingPeriod period, LocalDate from, LocalDate to) {
//
//        // 관리자 권한 검증
//        if (!"ADMIN".equals(authUser.getUserRole().name())) {throw new CustomException(ExceptionCode.NO_PERMISSION);}
//
//        // period 공백 검증
//        if (period == null || period.name().isBlank()) {period = RankingPeriod.REALTIME;}
//
//        // 기간 랭킹 허용 값 검증
//        if (period != RankingPeriod.REALTIME && period != RankingPeriod.DAILY && period != RankingPeriod.WEEKLY && period != RankingPeriod.MONTHLY) {throw new CustomException(ExceptionCode.INVALID_PERIOD);}
//
//        // 날짜 검증
//        if (from != null && to != null && from.isAfter(to)) {throw new CustomException(ExceptionCode.INVALID_DATE_RANGE);}
//
//        // period 공백 검증
//        LocalDateTime[] range = RankingPeriodPolicy.resolve(period, from, to);
//
//        // 기간 내 찜 기준 랭킹 집계
//        List<ProductPostFavoriteRankingAggregateResponse> favorite = productPostFavoriteRepository.findTopByFavoriteBetween(range[0], range[1], ProgressStatus.DOING.name(), PageRequest.of(0, 10));
//
//        AtomicInteger rank = new AtomicInteger(1);
//        List<ProductPostFavoriteRankingListResponse> result = favorite.stream()
//                .map(a ->
//                        ProductPostFavoriteRankingListResponse.from(
//                                rank.getAndIncrement(),
//                                a.getProductPostId(),
//                                a.getTitle(),
//                                a.getFavoriteCount()
//                        ))
//                .toList();
//
//        return result;
//    }
}
