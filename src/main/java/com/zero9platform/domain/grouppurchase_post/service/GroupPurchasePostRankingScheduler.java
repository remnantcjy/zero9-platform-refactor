package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupPurchasePostRankingScheduler {

    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * 서버가 완전히 실행된 후, 딱 한번 실행
     */
    @PostConstruct
    public void init() {
        refreshTotalRanking();
    }

    /**
     * 매일 DB로부터 Redis 랭킹 새로고침
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshTotalRanking() {

        // 기존 랭킹 ZSet삭제
        redisTemplate.delete("gpp:ranking:total");

        // gpp 풀스캔, 자동순위정렬 ZSet에 추가
        groupPurchasePostRepository.findAll().forEach(gpp -> {
            redisTemplate.opsForZSet().add(
                    "gpp:ranking:total",
                    gpp.getId().toString(),
                    gpp.getViewCount()
            );
        });
    }

}
