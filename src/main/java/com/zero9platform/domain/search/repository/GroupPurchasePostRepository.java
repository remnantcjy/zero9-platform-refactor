package com.zero9platform.domain.search.repository;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupPurchasePostRepository extends JpaRepository<GroupPurchasePost, Long> {

    //상품 키워드 검색
    @Query("""
                SELECT g
                FROM GroupPurchasePost g
                WHERE g.productName LIKE CONCAT('%', :keyword, '%')
    """)
    Page<GroupPurchasePost> search(String keyword, Pageable pageable);


    //인플루언서가 등록한 상품 검색
    Page<GroupPurchasePost> findByUser(User user, Pageable pageable);
}
