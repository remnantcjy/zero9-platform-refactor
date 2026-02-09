package com.zero9platform.domain.grouppurchase_post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GroupPurchasePostViewCountRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String VIEW_COUNT_KEY_PREFIX = "gpp:view_count:";
    private static final String TOTAL_RANKING_KEY = "gpp:ranking:total";
    private static final String DAILY_RANKING_KEY_PREFIX = "gpp:ranking:daily:";

    /**
     * 조회수 증가 (Redis)
     * - 쓰기 캐싱 (개념적으로는 버퍼링과 유사하지만 다름)
     */
    public void increaseViewCountRedisCache(Long gppId) {
        // 조회수 쓰기 캐싱
        String key = VIEW_COUNT_KEY_PREFIX + gppId;
        redisTemplate.opsForValue().increment(key); // INCR는 Redis atomic (원자성이므로, 동시성을 제어)
        
        // 전체 누적 랭킹 증가
        redisTemplate.opsForZSet().incrementScore(TOTAL_RANKING_KEY, gppId.toString(), 1);

        // 오늘의 실시간 랭킹 증가
        String dailyKey = getTodayRankingKey();
        redisTemplate.opsForZSet().incrementScore(dailyKey, gppId.toString(), 1);

        // 오늘의 실시간 랭킹 TTL 설정
        redisTemplate.expire(dailyKey, Duration.ofHours(24)); // 48?
    }

    /**
     * 조회수 조회 (Redis)
     * - 아직 DB에 반영되지 않은 값(cached) 읽기
     */
    public long getCachedViewCount(Long gppId) {
        String key = VIEW_COUNT_KEY_PREFIX + gppId;
        String cached = redisTemplate.opsForValue().get(key);

        // Redis에 value가 없으면 0 반환
        return cached != null ? Long.parseLong(cached) : 0L;
    }

    /**
     * 오늘 랭킹 Key
     * 예: gpp:ranking:daily:20260206
     */
    private String getTodayRankingKey() {
        return DAILY_RANKING_KEY_PREFIX + LocalDate.now();
    }

//    /**
//     * 조회수 증가 (중복 조회 방지 ver)
//     */
//    public void increaseViewCountRedis(Long gppId, Long userId) {
//        // DB에 접근 하지않으며, 원자적으로 조회수를 증가(동시성 발생 X)
//
//        // 어떤 gpp를 누가 봤는지
//        String viewedKey = VIEWED_KEY_PREFIX + ":" + gppId + ":" + userId;
//
//        // 중복 조회 방지
//        Boolean isFirstView = redisTemplate.opsForValue().setIfAbsent(viewedKey, "1", Duration.ofHours(24));
//
//        // 이미 조회한 사용자의 경우
//        if (Boolean.FALSE.equals(isFirstView)) {
//            return;
//        }
//        // 그렇지 않은 경우
//        redisTemplate.opsForValue().increment(VIEW_COUNT_KEY_PREFIX + ":" + gppId);
//
//    }

}
