package com.zero9platform.domain.activity_feed.repository;

import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {

    // 동일한 건으로 피드 생성 방지 (중복방지)
    boolean existsByTypeAndTargetId(String type, Long targetId);

    // 피드 개인화
    // 사용자가 찜한 상품들의 소식과 전체소식 통합 조회
    @Query("SELECT f FROM ActivityFeed f " +
            "WHERE f.targetId IN :interestIds " +
            "OR f.type = 'NOTICE' " +
            "ORDER BY f.createdAt DESC")
    Page<ActivityFeed> findFeedsByInterest(@Param("interestIds") List<Long> interestIds, Pageable pageable);
}
