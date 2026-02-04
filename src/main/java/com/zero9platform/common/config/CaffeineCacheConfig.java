package com.zero9platform.common.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class CaffeineCacheConfig {

    /**
     * 실시간 랭킹
     */
    @Bean
    public Cache<String, Long> realtimeRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .maximumSize(100_000)
                .build();
    }

    /**
     * 일간 랭킹 - 최소 24시간 유지
     */
    @Bean
    public Cache<String, Long> dailyRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(25, TimeUnit.HOURS)
                .maximumSize(200_000)
                .build();
    }

    /**
     * 주간 랭킹 (7일 누적)
     */
    @Bean
    public Cache<String, Long> weeklyRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(8, TimeUnit.DAYS)
                .maximumSize(300_000)
                .build();
    }

    /**
     * 월간 랭킹 캐시
     */
    @Bean
    public Cache<String, Long> monthlyRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(32, TimeUnit.DAYS)
                .maximumSize(500_000)
                .build();
    }

}
