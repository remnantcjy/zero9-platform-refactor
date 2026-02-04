package com.zero9platform.domain.ranking.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.zero9platform.common.enums.RankingPeriod;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RankingCounter {

    private static final String SEARCH = "SEARCH";
    private static final String FAVORITE = "FAVORITE";

    private final Map<RankingPeriod, Cache<String, Long>> cacheMap;

    public RankingCounter(
            @Qualifier("realtimeRankingCache") Cache<String, Long> realtime,
            @Qualifier("dailyRankingCache") Cache<String, Long> daily,
            @Qualifier("weeklyRankingCache") Cache<String, Long> weekly,
            @Qualifier("monthlyRankingCache") Cache<String, Long> monthly
    ) {
        this.cacheMap = Map.of(
                RankingPeriod.REALTIME, realtime,
                RankingPeriod.DAILY, daily,
                RankingPeriod.WEEKLY, weekly,
                RankingPeriod.MONTHLY, monthly
        );
    }

    /**
     * 검색어 카운터 증가
     */
    public void increaseKeyword(String keyword) {
        Arrays.stream(RankingPeriod.values())
                .forEach(period -> {
                    String key = SEARCH + ":" + period.name() + ":" + keyword;
                    cacheMap.get(period).asMap().merge(key, 1L, Long::sum);
                });
    }

    /**
     * 공동구매 상품 찜 카운터 증가
     */
    public void increaseFavorite(Long productPostId) {
        Arrays.stream(RankingPeriod.values()).forEach(period -> {
            String key = FAVORITE + ":" + period.name() + ":" + productPostId;
            cacheMap.get(period).asMap().merge(key, 1L, Long::sum);
            log.info("[RANKING][FAVORITE][REMOVE] key={}", key);
        });
    }


    /**
     * 공동구매 상품 찜 카운터 감소
     */
    public void decreaseFavorite(Long productPostId) {
        Arrays.stream(RankingPeriod.values()).forEach(period -> {
            Cache<String, Long> cache = cacheMap.get(period);
            String key = FAVORITE + ":" + period.name() + ":" + productPostId;

            cache.asMap().compute(key, (k, v) -> {
                if (v == null || v <= 1) {
                    log.info("[RANKING][FAVORITE][REMOVE] key={}, value={}", k, v);
                    return null;
                }
                return v - 1;
            });

            log.info("[RANKING][FAVORITE][DECREASE] key={}, value={}",
                    key, cache.getIfPresent(key));
        });
    }

    /**
     * 검색어 캐시 조회
     */
    public Map<String, Long> searchCounters(RankingPeriod period) {
        return cacheMap.get(period).asMap().entrySet().stream()
                .filter(e -> e.getKey().startsWith(SEARCH + ":" + period.name()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 찜 캐시 조회
     */
    public Map<String, Long> favoriteCounters(RankingPeriod period) {
        return cacheMap.get(period).asMap().entrySet().stream()
                .filter(e -> e.getKey().startsWith(FAVORITE + ":" + period.name()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * keyword 파싱
     */
    public String extractKeyword(String key) {
        String[] parts = key.split(":");
        return parts[parts.length - 1];
    }

    /**
     * productPostId 파싱
     */
    public Long extractProductPostId(String key) {
        String[] parts = key.split(":");
        return Long.valueOf(parts[parts.length - 1]);
    }

}
