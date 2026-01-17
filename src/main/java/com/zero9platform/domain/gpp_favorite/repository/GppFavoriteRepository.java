package com.zero9platform.domain.gpp_favorite.repository;

import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GppFavoriteRepository extends JpaRepository<GppFavorite, Long> {

    boolean existsByUserAndGroupPurchasePost(User user, GroupPurchasePost groupPurchasePost);

    Page<GppFavorite> findByUserId(Long userId, PageRequest pageRequest);
}