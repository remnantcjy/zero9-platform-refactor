package com.zero9platform.domain.gpp_favorite.repository;

import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GppFavoriteRepository extends JpaRepository<GppFavorite, Long> {

    // 찜 등록 확인용
    Optional<GppFavorite> findByUserIdAndGroupPurchasePostId(Long userId, Long gppId);

    // 찜 등록 중복 방지용
    boolean existsByUserIdAndGroupPurchasePostId(Long gppId, Long userId);

    // 본인 낌리스트 조회용
    Page<GppFavorite> findByUserId(Long id, Pageable pageable);
}