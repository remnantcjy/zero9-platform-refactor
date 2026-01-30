package com.zero9platform.domain.user.Service;

import com.zero9platform.domain.user.entity.Influencer;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.InfluencerRepository;
import com.zero9platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDummyDataService {

    private final InfluencerRepository influencerRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    /**
     * 회원 더미데이터 생성 (배치 처리)
     */
    @Transactional
    public void createDummyUsers(int count) {

        List<User> users = new ArrayList<>(1000);
        List<Influencer> influencers = new ArrayList<>(200);

        for (int i = 1; i <= count; i++) {

            boolean isInfluencer = i % 5 == 0; // 20%

            String prefix = isInfluencer ? "influencer" : "user";

            User user = new User(
                    prefix + "_" + String.format("%06d", i),   // loginId
                    "DUMMY_PASSWORD",                           // 암호화 제거
                    prefix + "_" + i + "@test.com",             // email
                    (isInfluencer ? "인플루언서" : "유저") + i, // name (20자 이하)
                    isInfluencer ? "INFLUENCER" : "USER",
                    "010" + String.format("%08d", i),           // phone (중복 없음)
                    prefix + "_nick_" + i                       // nickname
            );

            users.add(user);

            if (isInfluencer) {
                influencers.add(
                        new Influencer(
                                user,
                                "https://instagram.com/test" + i
                        )
                );
            }

            // 1000건 단위 배치 처리
            if (i % 1000 == 0) {
                flushAndClear(users, influencers);
            }
        }

        // 🔥 남은 데이터 처리
        flushAndClear(users, influencers);
    }

    private void flushAndClear(List<User> users, List<Influencer> influencers) {
        userRepository.saveAll(users);
        influencerRepository.saveAll(influencers);

        entityManager.flush(); // SQL 즉시 실행
        entityManager.clear(); // 1차 캐시 초기화

        users.clear();
        influencers.clear();
    }
}
