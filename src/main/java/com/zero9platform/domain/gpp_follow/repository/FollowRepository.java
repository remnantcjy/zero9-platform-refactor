package com.zero9platform.domain.gpp_follow.repository;

import com.zero9platform.domain.gpp_follow.entity.GppFollow;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<GppFollow, Long> {

    // 구독 관계 (사용자, 공동구매 게시물) 확인
    boolean existsByUserIdAndGroupPurchasePostId(Long userId, Long groupPurchasePostId);

    // 구독 관계 (사용자, 공동구매 게시물) 반환
    Optional<GppFollow> findByUserIdAndGroupPurchasePostId(Long userId, Long groupPurchasePostId);


}
