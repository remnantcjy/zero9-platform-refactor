package com.zero9platform.domain.activity_feed.repository;

import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {
    // 공용인지 개인피드인지 구분
    Optional<ActivityFeed> findFirstByTypeAndTargetIdAndUserIdOrderByCreatedAtDesc(String type, Long targetId, Long userId);

    // 찜이 없는 유저
    Page<ActivityFeed> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 피드 개인화
    // 내 피드 조회
    @Query("SELECT f FROM ActivityFeed f " +
            "WHERE (f.targetId IN :favoriteList AND f.userId IS NULL) " +
            "OR (f.userId = :userId) " +
            "ORDER BY f.createdAt DESC")
    Page<ActivityFeed> findFeedsByFavoriteListOrUserId(
            @Param("favoriteList") List<Long> favoriteList,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
