package com.zero9platform.global;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.GppApprovalStatus;
import com.zero9platform.common.enums.GppProgressStatus;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.influencer.entity.Influencer;
import com.zero9platform.domain.influencer.repository.InfluencerRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
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
    private final InfluencerRepository influencerRepository;

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
                        "user01",
                        "password",
                        "user01@test.com",
                        "홍길동",
                        "USER",
                        "010-1111-1111",
                        "길동이",
                        "https://instagram.com/user01"
                )
        );

        User user2 = userRepository.save(
                new User(
                        "user02",
                        "password",
                        "user02@test.com",
                        "김철수",
                        "USER",
                        "010-2222-2222",
                        "철수형",
                        "https://instagram.com/user02"
                )
        );

        User user3 = userRepository.save(
                new User(
                        "user03",
                        "password",
                        "user03@test.com",
                        "이영희",
                        "USER",
                        "010-3333-3333",
                        "영희짱",
                        "https://instagram.com/user03"
                )
        );

        User user4 = userRepository.save(
                new User(
                        "user04",
                        "password",
                        "user04@test.com",
                        "홍금보",
                        "USER",
                        "010-4444-4444",
                        "금보형",
                        null
                )
        );

        User admin = userRepository.save(
                new User(
                        "admin01",
                        "password",
                        "admin@test.com",
                        "관리자",
                        "ADMIN",
                        "010-5555-5555",
                        "관리자계정",
                        null
                )
        );

         /* =======================
           Influencer 더미 데이터
        ======================= */
        Influencer influencer1 = influencerRepository.save(
                new Influencer(user1)
        );

        Influencer influencer2 = influencerRepository.save(
                new Influencer(user2)
        );

        Influencer influencer3 = influencerRepository.save(
                new Influencer(user3)
        );

        // 승인 처리 (테스트용)
        influencer1.influencerApprove(true);   // 승인 O
        influencer2.influencerApprove(true);   // 승인 O
        influencer3.influencerApprove(false);  // 승인 X

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