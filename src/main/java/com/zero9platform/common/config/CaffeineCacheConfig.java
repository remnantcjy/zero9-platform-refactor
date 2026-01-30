package com.zero9platform.common.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    @Bean
    public Cache<String, Long> caffeineCache() {

        return Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS) // 기간 랭킹
                .maximumSize(100_000)
                .build();
    }
}
