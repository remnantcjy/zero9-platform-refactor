package com.zero9platform.domain.influencer.repository;

import com.zero9platform.common.enums.UserRole;
import com.zero9platform.domain.influencer.entity.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InfluencerRepository extends JpaRepository<Influencer, Long> {

    /**
     * 인플루언서 목록 조회
     */
    @Query("""
        SELECT i FROM Influencer i JOIN i.user u
        WHERE (:approved IS NULL OR i.influencerApprovalStatus = :approved) AND u.role = 'INFLUENCER'
    """)
    List<Influencer> findByApprovalStatusAndUser(@Param("approved") Boolean approved);

    /**
     * 인플루언서 user_id 조회
     */
    Optional<Influencer> findByUserId(Long userId);
}
