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
import com.zero9platform.domain.user_influencer.entity.UserInfluencer;
import com.zero9platform.domain.user_influencer.entity.UserInfluencerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
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
        User testUser = userRepository.save(
                new User(
                        "testUserid1",
                        passwordEncoder.encode("awdfth1324"),
                        "test0000@email.com",
                        "대박곱창 사장님",
                        "INFLUENCER",
                        "010-1112-1112",
                        "대박곱창 사장님",
                        "https://instagram.com/user"
                )
        );

        User user = userRepository.save(
                new User(
                        "testid1",
                        passwordEncoder.encode("awdfth1324"),
                        "test@email.com",
                        "곽두철",
                        "INFLUENCER",
                        "010-1113-1113",
                        "곽두철닉네임",
                        "https://instagram.com/user"
                )
        );

        User user1 = userRepository.save(
                new User(
                        "user01",
                        passwordEncoder.encode("user1@1324"),
                        "user01@test.com",
                        "홍길동",
                        "INFLUENCER",
                        "010-1111-1111",
                        "길동이",
                        "https://instagram.com/user01"
                )
        );

        User user2 = userRepository.save(
                new User(
                        "user02",
                        passwordEncoder.encode("user2@1324"),
                        "user02@test.com",
                        "김철수",
                        "INFLUENCER",
                        "010-2222-2222",
                        "철수형",
                        "https://instagram.com/user02"
                )
        );

        User user3 = userRepository.save(
                new User(
                        "user03",
                        passwordEncoder.encode("user3@1324"),
                        "user03@test.com",
                        "이영희",
                        "INFLUENCER",
                        "010-3333-3333",
                        "영희짱",
                        "https://instagram.com/user03"
                )
        );

        User user4 = userRepository.save(
                new User(
                        "user04",
                        passwordEncoder.encode("user4@1324"),
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
                        passwordEncoder.encode("admin1@1324"),
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
        Influencer testInfluencer = influencerRepository.save(
                new Influencer(testUser)
        );

        Influencer influencer = influencerRepository.save(
                new Influencer(user)
        );

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
        testInfluencer.influencerApprove(true); // 승인 O
        influencer.influencerApprove(true);   // 승인 O
        influencer1.influencerApprove(true);   // 승인 O
        influencer2.influencerApprove(true);   // 승인 O
        influencer3.influencerApprove(false);  // 승인 X

        /* =======================
   UserInfluencer 더미 데이터
======================= */
        userInfluencerRepository.save(
                new UserInfluencer(
                        testUser,
                        true,
                        LocalDateTime.now()
                )
        );

        userInfluencerRepository.save(
                new UserInfluencer(
                        user,
                        true,
                        LocalDateTime.now()
                )
        );

        userInfluencerRepository.save(
                new UserInfluencer(
                        user1,
                        true,
                        LocalDateTime.now()
                )
        );

        userInfluencerRepository.save(
                new UserInfluencer(
                        user2,
                        true,
                        LocalDateTime.now()
                )
        );

        userInfluencerRepository.save(
                new UserInfluencer(
                        user3,
                        false, // 승인 안된 인플루언서
                        null
                )
        );

        /* =======================
           GroupPurchasePost 더미
        ======================= */
        gppRepository.save(
                new GroupPurchasePost(
                        user,
                        "대박곱창",
                        "곱창 계의 전설, 오늘만 이 가격",
                        "https://image.test/daebak-gopchang.jpg",
                        20000L,
                        "https://buy.test/daebak-gopchang",
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