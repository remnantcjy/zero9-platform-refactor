package com.zero9platform.global.init;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.GppApprovalStatus;
import com.zero9platform.common.enums.GppProgressStatus;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.search.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import com.zero9platform.domain.user_influencer.entity.UserInfluencer;
import com.zero9platform.domain.user_influencer.entity.UserInfluencerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
//@Profile("local")
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GroupPurchasePostRepository gppRepository;
    private final UserInfluencerRepository userInfluencerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 중복방지
//        if (userRepository.count() > 0) {
//            return;
//        }

         /* =======================
           User 더미 데이터
        ======================= */
        User user1 = userRepository.save(
                new User(
                        "user1",
                        "password",
                        "user1@test.com",
                        "홍길동",
                        "USER",
                        "01011112222",
                        "엘튼존"
                )
        );

        User user2 = userRepository.save(
                new User(
                        "user2",
                        "password",
                        "user2@test.com",
                        "김철수",
                        "USER",
                        "01033334444",
                        "존레논"
                )
        );

        User user3 = userRepository.save(
                new User(
                        "user3",
                        "password",
                        "user3@test.com",
                        "상만덕",
                        "USER",
                        "01055556666",
                        "마산주먹"
                )
        );

        /* =======================
           Influencer 더미 데이터
        ======================= */
        userInfluencerRepository.save(
                new UserInfluencer(user1, true, LocalDateTime.now())
        );
        userInfluencerRepository.save(
                new UserInfluencer(user2, true, LocalDateTime.now())
        );
        userInfluencerRepository.save(
                new UserInfluencer(user3, true, LocalDateTime.now())
        );

        /* =======================
           GroupPurchasePost 더미
        ======================= */
        gppRepository.save(
                new GroupPurchasePost(
                        user1,
                        "서울 두쫀쿠",
                        "두쫀 두쫀 서울 두쫀쿠 공동구매",
                        "https://image.test/duzzoncu-seoul.jpg",
                        15000L,
                        "https://buy.test/seoul",
                        Category.ETC,
                        GppApprovalStatus.PENDING,
                        GppProgressStatus.DOING,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7)
                )
        );

        gppRepository.save(
                new GroupPurchasePost(
                        user1,
                        "인천 두쫀쿠",
                        "두쫀 두쫀 인천 두쫀쿠 공동구매",
                        "https://image.test/duzzoncu-incheon.jpg",
                        20000L,
                        "https://buy.test/incheon",
                        Category.ETC,
                        GppApprovalStatus.PENDING,
                        GppProgressStatus.DOING,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7)
                )
        );

        gppRepository.save(
                new GroupPurchasePost(
                        user2,
                        "아산 두쫀쿠",
                        "두쫀 두쫀 아산 두쫀쿠 공동구매",
                        "https://image.test/duzzoncu-asan.jpg",
                        8000L,
                        "https://buy.test/asan",
                        Category.ETC,
                        GppApprovalStatus.PENDING,
                        GppProgressStatus.DOING,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(5)
                )
        );

        gppRepository.save(
                new GroupPurchasePost(
                        user3,
                        "부산 두쫀쿠",
                        "두쫀 두쫀 부산 두쫀쿠 공동구매",
                        "https://image.test/duzzoncu-busan.jpg",
                        10000L,
                        "https://buy.test/busan",
                        Category.ETC,
                        GppApprovalStatus.PENDING,
                        GppProgressStatus.DOING,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(3)
                )
        );

        gppRepository.save(
                new GroupPurchasePost(
                        user3,
                        "응급실 떡볶이",
                        "응급실 떡볶이 공동구매",
                        "https://image.test/tteokbokki.jpg",
                        9000L,
                        "https://buy.test/tteokbokki",
                        Category.ETC,
                        GppApprovalStatus.PENDING,
                        GppProgressStatus.DOING,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(10)
                )
        );
    }
}
