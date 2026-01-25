package com.zero9platform.domain.product_post.repository;

import com.zero9platform.domain.product_post.entity.ProductPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductPostRepository extends JpaRepository<ProductPost, Long> {

    Optional<ProductPost> findByIdAndDeletedAtIsNull(Long productpostId);

    Page<ProductPost> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("""
        SELECT p
        FROM ProductPost p
        WHERE p.deletedAt IS NULL
          AND p.title LIKE CONCAT('%', :keyword, '%')
        ORDER BY p.createdAt DESC
    """)
    Page<ProductPost> search(@Param("keyword") String keyword, Pageable pageable);
}
