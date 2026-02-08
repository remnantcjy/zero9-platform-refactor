package com.zero9platform.domain.product_post.service;

import com.zero9platform.common.enums.Category;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
public class ProductPostStatusServiceTest {

    @Autowired ProductPostStatusService productPostStatusService;
    @Autowired ProductPostRepository productPostRepository;
    @Autowired UserRepository userRepository;

    @PersistenceContext
    private EntityManager em; // 데이터 삽입 시 메모리 관리를 위해 필요

    private final LocalDateTime now = LocalDateTime.of(2026, 2, 5, 18, 50);

    @BeforeEach
    void setUp() {
        // 자식 테이블(ProductPost)부터 삭제 후 부모 테이블(User) 삭제 (외래키 제약 방지)
        productPostRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        // 영속성 컨텍스트 초기화 (이전 테스트의 잔상 제거)
        em.clear();
    }

    void setupData(int size) {
        User user = new User(
                "admin",
                "encoded_password",
                "admin@email.com",
                "관리자",
                "ADMIN",
                "010-0000-0000",
                "관리자"
        );
        userRepository.save(user);

        for (int i = 0; i < size; i++) {
            LocalDateTime startDate;
            LocalDateTime endDate;

            if (i < size / 3) { startDate = now.plusDays(1); endDate = now.plusDays(3); }
            else if (i < size * 2 / 3) { startDate = now.minusDays(1); endDate = now.plusDays(1); }
            else { startDate = now.minusDays(3); endDate = now.minusDays(1); }

            ProductPost productPost = new ProductPost(
                    user, "상품 " + i, "내용", "상세", 42000L,
                    null, Category.BEAUTY.name(), startDate, endDate, now
            );

            ProductPostOption option = new ProductPostOption(productPost, "옵션", 35000L, 5);
            productPost.addOption(option);

            productPostRepository.save(productPost);

            // 메모리 부족 방지를 위해 5,000건마다 비워주기
            if (i % 5000 == 0) {
                em.flush();
                em.clear();

                // clear 후에는 user 객체가 준영속 상태가 되므로 다시 불러와야 함
                user = userRepository.findById(user.getId()).orElseThrow();
            }
        }
        productPostRepository.flush();
    }

    @Test
    @Transactional
    void 상품게시물_JPA연산_상태변경_소요시간_테스트() {

        int size = 100_000;
        setupData(size);

        // 현재 시간으로부터 2일 뒤를 '현재'라고 가정 (상태 변화 유도)
        // 2일 뒤면 시작일(1일 뒤)이 지났으므로 READY -> DOING으로 바뀜
        LocalDateTime simulationTime = now.plusDays(2);

        long start = System.currentTimeMillis();

        // JPA 연산 실행
        productPostStatusService.updateProgressStatusIndividually(simulationTime);

        // 실제 DB 반영 시간까지 포함하기 위해 flush 호출
        productPostRepository.flush();

        long end = System.currentTimeMillis();

        System.out.println("---------------------------------");
        System.out.println("⏱ 실제 상태 변경 소요 시간(ms) = " + (end - start));
        System.out.println("---------------------------------");
    }

    @Test
    @Transactional
    void 상품게시물_벌크연산_상태변경_소요시간_테스트() {

        int size = 100_000;
        setupData(size);

        LocalDateTime simulationTime = now.plusDays(2);

        long start = System.currentTimeMillis();

        // 벌크 연산 실행
        productPostRepository.updateToDoing(simulationTime);
        productPostRepository.updateToEnd(simulationTime);

        long end = System.currentTimeMillis();

        System.out.println("---------------------------------");
        System.out.println("⚡ 벌크 연산 소요 시간(ms) = " + (end - start));
        System.out.println("---------------------------------");
    }
}