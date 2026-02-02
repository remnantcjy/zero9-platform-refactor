package com.zero9platform.common.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    /**
     * 실시간 랭킹 캐시
     */
    @Bean
    public Cache<String, Object> realtimeRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES) // TTL = 1분
                .maximumSize(500)
                .build();
    }

    /**
     * 일간 랭킹 캐시
     */
    @Bean
    public Cache<String, Object> dailyRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES) // TTL = 30분
                .maximumSize(500)
                .build();
    }

    /**
     * 주간 랭킹 캐시
     */
    @Bean
    public Cache<String, Object> weeklyRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS) // TTL = 2시간
                .maximumSize(500)
                .build();
    }

    /**
     * 월간 랭킹 캐시
     */
    @Bean
    public Cache<String, Object> monthlyRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS) // TTL = 12시간
                .maximumSize(500)
                .build();
    }
}
