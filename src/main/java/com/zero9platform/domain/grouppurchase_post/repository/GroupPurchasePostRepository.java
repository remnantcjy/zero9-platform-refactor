package com.zero9platform.domain.grouppurchase_post.repository;

import com.zero9platform.common.enums.GppApprovalStatus;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupPurchasePostRepository extends JpaRepository<GroupPurchasePost,Long> {

    // 공동구매 게시물 상세 조회 - [삭제처리 제외]
    Optional<GroupPurchasePost> findByIdAndDeletedAtIsNull(Long id);

    // 공동구매 게시물 상세 조회 - [삭제처리 제외 + 승인된 공동구매 게시물]
    Optional<GroupPurchasePost> findByIdAndDeletedAtIsNullAndGppApprovalStatus(Long id, GppApprovalStatus gppApprovalStatus);

    // 공동구매 게시물 페이징 조회 [삭제처리 제외 + 승인된 공동구매 게시물]
    Page<GroupPurchasePost> findAllByDeletedAtIsNullAndGppApprovalStatus(Pageable pageable, GppApprovalStatus gppApprovalStatus);

    // 조회 수 증가 - 삭제처리된 대상은 제외
    // DB에서 직접 증가, 영속성 컨텍스트를 거치지 않음
    @Modifying(clearAutomatically = true)

    //상품 키워드 검색
    @Query("""
                update GroupPurchasePost g
                set g.viewCount = g.viewCount + 1
                where g.id = :gppId and g.deletedAt is null
            """)
    int increaseViewCount(@Param("gppId") Long gppId);


    //통합 상품 키워드 검색
    @Query("""
                SELECT g
                FROM GroupPurchasePost g
                WHERE g.productName LIKE CONCAT('%', :keyword, '%')
    """)
    Page<GroupPurchasePost> search(String keyword, Pageable pageable);


    //인플루언서가 등록한 상품 검색
    Page<GroupPurchasePost> findByUser(User user, Pageable pageable);
}
