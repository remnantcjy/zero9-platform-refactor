package com.zero9platform.domain.order.repository;

import com.zero9platform.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByOrderItem_User_Id(Long userId, Pageable pageable);

    /**
     * 주문 번호 조회
     */
    Optional<Order> findByOrderNo(String orderNo);

    // 특정 상품 ID를 가진 주문 아이템의 전체 개수를 셉니다.
    long countByOrderItem_ProductPost_Id(Long productId);
}
