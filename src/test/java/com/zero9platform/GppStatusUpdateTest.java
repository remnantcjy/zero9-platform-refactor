package com.zero9platform;

import com.zero9platform.common.enums.Category;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
public class GppStatusUpdateTest {

    @Autowired
    GroupPurchasePostRepository groupPurchasePostRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    // flush, clear를 쓰기 위한 엔티티 메니저
    @Autowired
    EntityManager entityManager;

    @Test
    void 모집상태_일괄_Update() {

        // 테스트를 위한 가짜 현재시각 - 2026-02-02 00시
        LocalDateTime baseDate = LocalDateTime.of(2026, 2, 2, 0, 0);

        // ---------------------------------------------- Given
        // 테스트용 유저
        String uuid = UUID.randomUUID().toString();
        User influencerUser = userRepository.save(
                new User(
                        "login_" + uuid,
                        passwordEncoder.encode("influencer1@1324"),
                        uuid + "@test.com",
                        "박인플",
                        "INFLUENCER",
                        "010-" + uuid.substring(0, 8),
                        "인플루언서박1"
                )
        );

        // 공동구매 게시물 더미 10000개
        //
        for (int i = 0; i < 10000; i++) {
            GroupPurchasePost gpp = new GroupPurchasePost(
                    influencerUser,
                    "상품" + i,
                    "내용",
                    null,
                    10000L,
                    "https://zep.us/play/nL9kqr",
                    Category.ETC.name(),
                    baseDate.plusDays(1),
                    baseDate.plusDays(2),
                    baseDate
            );
            groupPurchasePostRepository.save(gpp);
        }

        // JPA 컨테이너에서 영속성 상태로 관리를 받는 데이터는 clear하지 않으면 계속 남으니까, 지워주고 실행해야한다
        // 더미를 생성한 후와 실제 성능 측정 대상인 조회+변경 사이에 flush+clear를 해준다
        // 측정 대상 실행 전 : 변경사항을 즉시 반영 + JPA 영속 컨테이너(캐시) 리셋 + DB기준으로 재실행

        // Dirty Checking + 즉시 Update 쿼리 실행 (트랜잭션은 안끝났으므로, 커밋이 아님)
        entityManager.flush();
        // 메모리에 있던 엔티티 재사용 방지(비영속 상태로 변경/캐시 날리기), 매번 DB를 조회하게끔 설정 (실제 성능 측정)
        entityManager.clear();

        // 3일 경과한 시점
        LocalDateTime after3Days = baseDate.plusDays(3);

        // ---------------------------------------------- When
        long start = System.currentTimeMillis(); // 시간 측정 시작

        // 가짜 현재시간 기준으로 대상 타켓 조회 실행
        List<GroupPurchasePost> targets = groupPurchasePostRepository.findProgressStatusChangeTargets(after3Days);
        // 각 타겟의 모집상태를 모두 변경
        targets.forEach(gpp -> gpp.updateProgressStatus(after3Days));

        entityManager.flush(); // 실제 UPDATE 발생

        long end = System.currentTimeMillis(); // 시간 측정 종료

        // Then
        System.out.println("변경 대상 수 = " + targets.size());
        System.out.println("상태 변경 소요 시간(ms) = " + (end - start));

    }

}
