package com.zero9platform.domain.grouppurchase_post.repository;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GppRepository extends JpaRepository<GroupPurchasePost, Long> {

    @Query("select gpp from GroupPurchasePost gpp join fetch GppFollow gppFollow " +
            "on gpp.id = gppFollow.groupPurchasePost.id " +
            "where gppFollow.user.id = :userId " +
            "and gpp.deletedAt is null")
    Page<GroupPurchasePost> findByUserIdAndFollowGpp(@Param("userId") Long userId, Pageable pageable);
}
