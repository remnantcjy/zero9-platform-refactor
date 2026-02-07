package com.zero9platform.domain.ranking.entity;

import com.zero9platform.common.enums.RankingPeriod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "keyword_ranking_snapshots", uniqueConstraints = {
                @UniqueConstraint(name = "uk_keyword_period_date", columnNames = {"keyword", "period", "target_date"})}
)
public class FavoriteRankingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 게시물 ID
    @Column(name = "product_post_id", nullable = false)
    private Long productPostId;

    // 랭킹 기준 기간
    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private RankingPeriod period;

    // 집계된 찜 개수 (캐시 스냅샷 값)
    @Column(nullable = false)
    private Long favoriteCount;

    @Column(name = "target_date", nullable = false)
    private String targetDate;

    // 스냅샷 생성 시각
    @Column(nullable = false)
    private LocalDateTime snapshotAt;

    public FavoriteRankingSnapshot(Long productPostId, RankingPeriod period, Long favoriteCount, String targetDate) {
        this.productPostId = productPostId;
        this.period = period;
        this.favoriteCount = favoriteCount;
        this.targetDate = targetDate;
        this.snapshotAt = LocalDateTime.now();
    }
}
