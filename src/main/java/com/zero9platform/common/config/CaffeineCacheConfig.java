package com.zero9platform.common.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zero9platform.domain.ranking.model.response.ProductPostFavoriteRankingListResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    @Bean
    public Cache<String, List<ProductPostFavoriteRankingListResponse>> caffeineCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES) // 1분 랭킹 캐시
                .maximumSize(100)
                .build();
    }
}
