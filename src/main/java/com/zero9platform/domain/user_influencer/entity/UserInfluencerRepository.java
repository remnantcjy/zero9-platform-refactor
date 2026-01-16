package com.zero9platform.domain.user_influencer.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserInfluencerRepository extends JpaRepository<UserInfluencer, Integer> {

    @Query("""
                SELECT ui
                FROM UserInfluencer ui
                JOIN ui.user u
                WHERE u.nickname = :nickname
            """)
    Optional<UserInfluencer> findByNickname(@Param("nickname") String keyword);
}
