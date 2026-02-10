package com.zero9platform.domain.activity_feed.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class ActivityFeedRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ActivityFeedRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        // Jackson 날짜 직렬화 에러 방지 설정
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // LocalDateTime 지원
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 포맷 유지
    }

    private static final String FEED_LIST_KEY_PREFIX = "user:feed:";
    private static final String ORDER_COUNT_KEY_PREFIX = "product:order:count:";

    /**
     * [전략 1] 피드 목록 캐시 조회 (역직렬화)
     * - Redis에 저장된 JSON 문자열을 Java 객체 리스트로 변환합니다.
     */
    public List<ActivityFeedResponse> getCachedFeeds(String key) {
        String json = redisTemplate.opsForValue().get(FEED_LIST_KEY_PREFIX + key);
        if (json == null) return null;

        try {
            return objectMapper.readValue(json, new TypeReference<List<ActivityFeedResponse>>() {});
        } catch (JsonProcessingException e) {
            log.error("피드 캐시 역직렬화 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * [전략 1] 피드 목록 캐시 저장 (직렬화)
     * - 9초 소요되던 조회 결과를 Redis에 10분간 보관합니다.
     */
    public void saveFeedCache(String key, List<ActivityFeedResponse> feeds) {
        try {
            String json = objectMapper.writeValueAsString(feeds);
            redisTemplate.opsForValue().set(FEED_LIST_KEY_PREFIX + key, json, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            log.error("피드 캐시 직렬화 실패: {}", e.getMessage());
        }
    }

    /**
     * [전략 2] 실시간 주문 카운팅 (Atomic INCR)
     * - 'Purchase' 대신 'Order' 명칭 사용.
     * - 동시성 이슈 없이 숫자를 올리고, 당일 자정에 자동 소멸되도록 설정합니다.
     */
    public void incrementOrderCount(Long productId) {
        String key = ORDER_COUNT_KEY_PREFIX + productId;
        Long count = redisTemplate.opsForValue().increment(key);

        // 첫 카운트 발생 시 만료 시간 설정 (당일 23:59:59)
        if (count != null && count == 1) {
            LocalDateTime midnight = LocalDateTime.now().with(LocalTime.MAX);
            redisTemplate.expireAt(key, midnight.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    /**
     * [전략 2] 실시간 주문 카운트 조회
     * - Redis에 저장된 최신 주문 수치를 가져옵니다.
     */
    public long getOrderCount(Long productId) {
        String key = ORDER_COUNT_KEY_PREFIX + productId;
        String countStr = redisTemplate.opsForValue().get(key);
        return countStr != null ? Long.parseLong(countStr) : 0L;
    }

    /**
     * [전략 3] 피드 캐시 삭제 (Evict)
     * - 새로운 피드가 생성되면 기존 캐시를 삭제하여 데이터 정합성을 유지합니다.
     */
    public void deleteFeedCache(String key) {
        redisTemplate.delete(FEED_LIST_KEY_PREFIX + key);
    }
}