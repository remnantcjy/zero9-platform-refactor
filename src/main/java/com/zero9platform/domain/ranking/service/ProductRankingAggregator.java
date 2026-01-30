package com.zero9platform.domain.ranking.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductRankingAggregator {

    private final Cache<String, Long> caffeineCache;

    /**
     * 메모리 집계 데이터를 상품별 점수로 변환
     */
    public Map<Long, Long> snapshotProductScore() {

        Map<Long, Long> result = new HashMap<>();

        caffeineCache.asMap().forEach((key, value) -> {

            // key: productId:123:view or productId:123:favorite
            String[] parts = key.split(":");
            Long productId = Long.valueOf(parts[1]);
            String type = parts[2];

            long score = type.equals("favorite") ? value * 5 : value;

            result.merge(productId, score, Long::sum);
        });

        return result;
    }
}
