package com.zero9platform.domain.activity_feed.entity;

import com.zero9platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "activity_feed")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityFeed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Long productPostId;

    public ActivityFeed(String type, String message, Long productPostId) {
        this.type = type;
        this.message = message;
        this.productPostId = productPostId;
    }
}
