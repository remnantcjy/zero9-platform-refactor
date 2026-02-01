package com.zero9platform.domain.grouppurchase_post.repository;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupPurchasePostRepository extends JpaRepository<GroupPurchasePost, Long> {

    // 공동구매 게시물 상세 조회 - [삭제처리 제외]
    Optional<GroupPurchasePost> findByIdAndDeletedAtIsNull(Long id);

    // 공동구매 게시물 페이징 조회 [삭제처리 제외]
    Page<GroupPurchasePost> findAllByDeletedAtIsNull(Pageable pageable);

    // 조회 수 증가 - 삭제처리된 대상은 제외
    // DB에서 직접 증가, 영속성 컨텍스트를 거치지 않음
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update GroupPurchasePost g
                set g.viewCount = g.viewCount + 1
                where g.id = :gppId and g.deletedAt is null
            """)
    int increaseViewCount(@Param("gppId") Long gppId);

    // ViewCount 랭킹 조회
    @Query("""
                SELECT g
                FROM GroupPurchasePost g
                WHERE g.gppProgressStatus = :status
                  AND g.createdAt BETWEEN :from AND :to
                  AND g.deletedAt IS NULL
                ORDER BY g.viewCount DESC
            """)
    List<GroupPurchasePost> findTopByViewCountBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("status") String status, Pageable pageable);
}
