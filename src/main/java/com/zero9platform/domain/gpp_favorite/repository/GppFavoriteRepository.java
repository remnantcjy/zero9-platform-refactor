package com.zero9platform.domain.gpp_favorite.repository;

import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GppFavoriteRepository extends JpaRepository<GppFavorite, Long> {

    boolean existsByUserAndGroupPurchasePost(User user, GroupPurchasePost groupPurchasePost);
}