package com.zero9platform.domain.ranking.repository;

import com.zero9platform.domain.ranking.entity.ProductRankingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductRankingSnapshotRepository extends JpaRepository<ProductRankingSnapshot, Long> {

    List<ProductRankingSnapshot> findTop10BySnapshotDateOrderByScoreDesc(LocalDateTime snapshotDate);
}
