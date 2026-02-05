package com.zero9platform.domain.ranking.service;

import com.zero9platform.common.enums.RankingPeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class RedisRankingCounter {

    private final StringRedisTemplate redisTemplate;

    private static final String SEARCH = "SEARCH";
    private static final String FAVORITE = "FAVORITE";
//    private final Map<RankingPeriod, Cache<String, Long>> cacheMap;

    /**
     * 검색어 카운터 증가
     */
    public void increaseKeyword(String keyword) {

        for (RankingPeriod period : RankingPeriod.values()) {

            if (period == RankingPeriod.REALTIME) continue;

            redisTemplate.opsForZSet()
                    .incrementScore(getNowKey(SEARCH, period), keyword, 1);

            // 안전장치: 각 기간보다 넉넉하게 만료 시간 설정
            // 일간(DAILY) 데이터라면 2~3일 뒤 자동 삭제
            if (period == RankingPeriod.DAILY) {
                redisTemplate.expire(getNowKey(SEARCH, period), Duration.ofDays(3));
            } else if (period == RankingPeriod.WEEKLY) {
                redisTemplate.expire(getNowKey(SEARCH, period), Duration.ofDays(14)); // 주간 데이터는 2주 뒤 삭제
            }
        }
    }

    /**
     * 공동구매 상품 찜 카운터 증가
     */
    public void increaseFavorite(Long productPostId) {

        for (RankingPeriod period : RankingPeriod.values()) {

            if (period == RankingPeriod.REALTIME) continue;

            redisTemplate.opsForZSet()
                    .incrementScore(getNowKey(FAVORITE, period), productPostId.toString(), 1);

            // 안전장치: 각 기간보다 넉넉하게 만료 시간 설정
            // 일간(DAILY) 데이터라면 2~3일 뒤 자동 삭제
            if (period == RankingPeriod.DAILY) {
                redisTemplate.expire(getNowKey(SEARCH, period), Duration.ofDays(3));
            } else if (period == RankingPeriod.WEEKLY) {
                redisTemplate.expire(getNowKey(SEARCH, period), Duration.ofDays(14)); // 주간 데이터는 2주 뒤 삭제
            }
        }
    }

    /**
     * 공동구매 상품 찜 카운터 감소
     */
    public void decreaseFavorite(Long productPostId) {

        for (RankingPeriod period : RankingPeriod.values()) {
            if (period == RankingPeriod.REALTIME) continue;
            Double score = redisTemplate.opsForZSet()
                    .incrementScore(getNowKey(FAVORITE, period), productPostId.toString(), -1);

            if (score != null && score <= 0) {
                redisTemplate.opsForZSet().remove(getNowKey(FAVORITE, period), productPostId.toString());
            }
        }
    }

    /**
     * 랭킹 조회 (Service에서 사용)
     */
    public Set<ZSetOperations.TypedTuple<String>> topRankings(String type, RankingPeriod period, int limit) {
        String key = (period == RankingPeriod.REALTIME) ? getNowKey(type, RankingPeriod.DAILY) : getNowKey(type, period);
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);
    }

    /**
     * 스냅샷용 조회 (특정 시점의 키를 조회하기 위함)
     */
    public Set<ZSetOperations.TypedTuple<String>> getRankingsBySpecificKey(String key) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }

    /**
     * 캐시메모리 초기화
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 현재 시점의 키 생성
     */
    public String getNowKey(String type, RankingPeriod period) {
        return generateKey(type, period, LocalDateTime.now());
    }

    /**
     * 날짜 패턴을 포함한 키 생성 로직
     */
    public String generateKey(String type, RankingPeriod period, LocalDateTime dateTime) {
        String datePattern = switch (period) {
            case DAILY -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));   // 예: 2024-05-22
            case WEEKLY -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-ww"));     // 예: 2024-W21 (ISO 주차 기준)
            case MONTHLY -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM"));    // 예: 2024-05
            default -> "TOTAL";
        };
        // 최종 검색어 키 형태: ranking:SEARCH:DAILY:2024-05-22
        // 최종 상품찜 키 형태: ranking:FAVORITE:WEEKLY:2026-W06
        return "ranking:" + type + ":" + period.name() + ":" + datePattern;
    }


}
