package com.zero9platform.domain.product_post.repository;

import com.zero9platform.domain.product_post.entity.ProductPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductPostRepository extends JpaRepository<ProductPost, Long> {

    Optional<ProductPost> findByIdAndDeletedAtIsNull(Long productpostId);

//    Page<ProductPost> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("select distinct pp from ProductPost pp " +
            "join pp.productPostOptionList o " +
            "where pp.deletedAt is null " +
            "and o.optionStatus = 'ACTIVE'")
    Page<ProductPost> findAllVisible(Pageable pageable);
}
