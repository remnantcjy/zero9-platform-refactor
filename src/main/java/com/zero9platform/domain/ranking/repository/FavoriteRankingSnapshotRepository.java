package com.zero9platform.domain.ranking.repository;

import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.domain.ranking.entity.FavoriteRankingSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRankingSnapshotRepository extends JpaRepository<FavoriteRankingSnapshot, Long> {

    // 상품 찜 랭킹 조회
    Page<FavoriteRankingSnapshot> findByPeriodAndTargetDateOrderByFavoriteCountDesc(RankingPeriod period, String targetDate, Pageable pageable);
}
