package com.zero9platform.domain.ranking.scheduler;

import com.github.benmanes.caffeine.cache.Cache;
import com.zero9platform.domain.ranking.entity.ProductRankingSnapshot;
import com.zero9platform.domain.ranking.repository.ProductRankingSnapshotRepository;
import com.zero9platform.domain.ranking.service.ProductRankingAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRankingScheduler {

    private final ProductRankingAggregator productRankingAggregator;
    private final ProductRankingSnapshotRepository productRankingSnapshotService;
    private final Cache<String, Long> caffeineCache;

    @Scheduled(cron = "0 0 1 * * *") // 매일 자정에 실행
    public void snapshotDailyRanking() {

        LocalDateTime today = LocalDateTime.now().minusDays(1);
        Map<Long, Long> scores = productRankingAggregator.snapshotProductScore();

        scores.forEach((productId, score) -> {
            productRankingSnapshotService.save(new ProductRankingSnapshot(productId, score, today)
            );
        });
        caffeineCache.invalidateAll();
        log.info("[RANKING] daily snapshot saved. cache cleared");
    }
}
