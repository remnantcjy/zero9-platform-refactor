package com.zero9platform.domain.gpp_favorite.repository;

import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GppFavoriteRepository extends JpaRepository<GppFavorite, Long> {

    Optional<GppFavorite> findByUserIdAndGroupPurchasePostId(Long userId, Long gppId);

    boolean existsByUserIdAndGroupPurchasePostId(Long gppId, Long userId);

    Page<GppFavorite> findByUserId(Long id, Pageable pageable);
}