package com.zero9platform.domain.grouppurchase_post.repository;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupPurchasePostRepository extends JpaRepository<GroupPurchasePost,Long> {

    // 상세 조회 - 삭제처리된 대상은 제외 <- 이거 근데 좀 이따 확인해야함
    Optional<GroupPurchasePost> findByIdAndDeletedAtIsNull(Long id);

    // 목록 전체 조회 - 삭제처리된 대상은 제외
    Page<GroupPurchasePost> findAllByDeletedAtIsNull(Pageable pageable);

    // 조회 수 증가 - 삭제처리된 대상은 제외
    // DB에서 직접 증가, 영속성 컨텍스트를 거치지 않음
    @Modifying(clearAutomatically = true)
    @Query("""
                update GroupPurchasePost g
                set g.viewCount = g.viewCount + 1
                where g.id = :gppId and g.deletedAt is null
            """)
    int increaseViewCount(@Param("gppId") Long gppId);

}
