package com.zero9platform.domain.ranking.scheduler;

import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.domain.ranking.entity.FavoriteRankingSnapshot;
import com.zero9platform.domain.ranking.entity.KeywordRankingSnapshot;
import com.zero9platform.domain.ranking.repository.FavoriteRankingSnapshotRepository;
import com.zero9platform.domain.ranking.repository.KeywordRankingSnapshotRepository;
import com.zero9platform.domain.ranking.service.RedisRankingCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Log4j2
@Component
@RequiredArgsConstructor
public class RankingFlushScheduler {

    private final RedisRankingCounter redisRankingCounter;
    private final FavoriteRankingSnapshotRepository favoriteRankingSnapshotRepository;
    private final KeywordRankingSnapshotRepository keywordRankingSnapshotRepository;

    /**
     * DAILY 랭킹 스냅샷 저장 - 실행 시점: 매일 자정 00시
     */
    @Transactional
    @Scheduled(cron = "0 5 0 * * *")
    public void flushDailyRanking() {

        processFlush(RankingPeriod.DAILY, LocalDateTime.now().minusDays(1));
    }

    /**
     * WEEKLY 스냅샷 저장 - 실행 시점: 매주 월요일 자정 00시
     */
    @Transactional
    @Scheduled(cron = "0 5 0 * * MON")
    public void flushWeeklyRanking() {

        processFlush(RankingPeriod.WEEKLY, LocalDateTime.now().minusWeeks(1));
    }

    /**
     * MONTHLY 스냅샷 저장 - 실행 시점: 매월 1일 자정 00시
     */
    @Transactional
    @Scheduled(cron = "0 15 0 1 * *") // 매월 1일 00:15
    public void flushMonthlyRanking() {

        processFlush(RankingPeriod.MONTHLY, LocalDateTime.now().minusMonths(1));
    }

    private void processFlush(RankingPeriod period, LocalDateTime targetTime) {

        String favoriteKey = redisRankingCounter.getNowKey("FAVORITE", period);
        String searchKey = redisRankingCounter.getNowKey("SEARCH", period);

        // 상품 찜 랭킹 스냅샷 저장
        redisRankingCounter.getRankingsBySpecificKey(favoriteKey).forEach(tuple -> {
            favoriteRankingSnapshotRepository.save(new FavoriteRankingSnapshot(
                    Long.parseLong(tuple.getValue()),
                    period,
                    tuple.getScore().longValue()));
        });
        redisRankingCounter.deleteKey(favoriteKey);


        // 검색어 랭킹 스냅샷 저장
        redisRankingCounter.getRankingsBySpecificKey(searchKey).forEach(tuple -> {
            keywordRankingSnapshotRepository.save(new KeywordRankingSnapshot(
                    tuple.getValue(),
                    period,
                    tuple.getScore().longValue()));
        });
        redisRankingCounter.deleteKey(searchKey);

        log.info("[RANKING-FLUSH] {} data for {} has been moved to DB and deleted from Redis", period, targetTime);

    }

//    /**
//     * 상품 찜(FAVORITE) 랭킹 스냅샷 저장
//     * period에 해당하는 캐시 데이터를 조회
//     * DB에 스냅샷 형태로 그대로 저장
//     */
//    private void saveFavorite(RankingPeriod period) {
//        redisRankingCounter.favoriteCounters(period).forEach((k, v) ->
//                favoriteRankingSnapshotRepository.save(new FavoriteRankingSnapshot(redisRankingCounter.extractProductPostId(k), period, v))
//        );
//        log.info("[SNAPSHOT][FAVORITE] {} saved", period);
//    }
//
//    /**
//     * 검색어(KEYWORD) 랭킹 스냅샷 저장
//     * period별 검색어 캐시 데이터 조회
//     * DB에 스냅샷 형태로 그대로 저장
//     */
//    private void saveKeyword(RankingPeriod period) {
//        redisRankingCounter.searchCounters(period).forEach((k, v) ->
//                keywordRankingSnapshotRepository.save(new KeywordRankingSnapshot(redisRankingCounter.extractKeyword(k), period, v))
//        );
//        log.info("[SNAPSHOT][KEYWORD] {} saved", period);
//    }
}