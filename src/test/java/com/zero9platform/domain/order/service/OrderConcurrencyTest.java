package com.zero9platform.domain.order.service;

import com.zero9platform.domain.orderitem.entity.OrderItem;
import com.zero9platform.domain.orderitem.repository.OrderItemRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderConcurrencyTest {

    @Autowired private OrderService orderService;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductPostRepository productPostRepository;
    @Autowired private ProductPostOptionRepository optionRepository;
    @Autowired private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("100명의 유저가 각자의 주문 아이템으로 하나의 재고(5개)에 도전하면 5명만 성공한다")
    void order_concurrency_test() throws InterruptedException {
        // [1. Given: 공통 데이터 준비]
        User user = userRepository.save(new User(
                "tester", "pw123", "test@test.com", "홍길동", "USER", "010-1234-5678", "nick"
        ));

        LocalDateTime now = LocalDateTime.now();
        ProductPost product = productPostRepository.save(new ProductPost(
                user, "벨르랑코 선크림 한정특가", "선크림", "공구 진행!", 42000L,
                "img.png", "BEAUTY", now.minusDays(1), now.plusDays(1), now
        ));

        // [중요] 재고는 딱 5개만 설정
        ProductPostOption option = optionRepository.save(new ProductPostOption(
                product, "벨르랑코 선크림", 35000L, 5
        ));

        // [중요] 100개의 각기 다른 OrderItem 생성 (100명이 각자 장바구니에 담아놓은 상태)
        List<Long> orderItemIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            OrderItem oi = orderItemRepository.save(new OrderItem(user, product, option, 1));
            orderItemIds.add(oi.getId());
        }

        Long userId = user.getId();
        Long optionId = option.getId();

        // [2. When: 100명 동시 주문 시뮬레이션]
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final int index = i; // 익명 객체 내 사용을 위한 final 변수
            executorService.submit(() -> {
                try {
                    // 각기 다른 orderItemId를 가지고 주문 호출
                    orderService.orderCreate(userId, orderItemIds.get(index));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // [3. Then: 결과 검증]
        ProductPostOption finalOption = optionRepository.findById(optionId).orElseThrow();

        System.out.println("=====================================");
        System.out.println("최종 주문 성공 (예상: 5): " + successCount.get());
        System.out.println("최종 주문 실패 (예상: 95): " + failCount.get());
        System.out.println("최종 남은 재고 (예상: 0): " + finalOption.getStockQuantity());
        System.out.println("최종 품절 상태 (예상: SOLD_OUT): " + finalOption.getStockStatus());
        System.out.println("=====================================");

        assertThat(successCount.get()).isEqualTo(5);
        assertThat(finalOption.getStockQuantity()).isEqualTo(0);
        assertThat(finalOption.getStockStatus()).isEqualTo("SOLD_OUT");
    }
}