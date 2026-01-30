package com.zero9platform.domain.ranking.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRankingCounter {

    private final Cache<String, Long> caffeineCache;

    public void increaseView(Long productId) {
        String key = "productId:" + productId + ":view";
        Long value = caffeineCache.asMap().merge(key, 1L, Long::sum);
        log.info("[RANKING-CACHE] {} = {}", key, value);
    }

    public void increaseFavorite(Long productId) {
        String key = "productId:" + productId + ":favorite";
        Long value = caffeineCache.asMap().merge(key, 1L, Long::sum);
        log.info("[RANKING-CACHE] {} = {}", key, value);
    }

    public void decreaseFavorite(Long productId) {
        String key = "productId:" + productId + ":favorite";
        Long value = caffeineCache.asMap().computeIfPresent(key, (k, v) -> Math.max(v - 1, 0));
        log.info("[RANKING-CACHE] {} = {}", key, value);
    }
}