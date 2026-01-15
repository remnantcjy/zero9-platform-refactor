package com.zero9platform.domain.user_influencer.entity;

import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user_influencers")
public class UserInfluencer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Boolean influencerApprovalStatus;

    @Column
    private LocalDateTime approvalAt;
}
