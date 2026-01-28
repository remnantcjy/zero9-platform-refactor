package com.zero9platform.domain.clickLog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "click_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClickLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productPostId;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private LocalDateTime clickedAt = LocalDateTime.now();

    public ClickLog(Long userId, Long productPostId, String keyword) {
        this.userId = userId;
        this.productPostId = productPostId;
        this.keyword = keyword;
    }
}