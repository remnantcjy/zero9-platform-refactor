package com.zero9platform.domain.ranking.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_ranking_snapshot")
public class ProductRankingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "score", nullable = false)
    private Long score;

    @Column(nullable = false)
    private LocalDateTime snapshotDate;

    public ProductRankingSnapshot(Long productId, Long score, LocalDateTime snapshotDate) {
        this.productId = productId;
        this.score = score;
        this.snapshotDate = snapshotDate;
    }
}
