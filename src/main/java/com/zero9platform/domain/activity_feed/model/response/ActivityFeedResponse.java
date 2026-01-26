package com.zero9platform.domain.activity_feed.model.response;

import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ActivityFeedResponse {
    private final Long id;
    private final String type;
    private final String message;
    private final Long productPostId;
    private final Long userId;
    private final LocalDateTime createdAt;

    public ActivityFeedResponse(Long id, String type, String message, Long productPostId, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.productPostId = productPostId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static ActivityFeedResponse from(ActivityFeed feed) {
        return new ActivityFeedResponse(
                feed.getId(),
                feed.getType(),
                feed.getMessage(),
                feed.getProductPostId(),
                feed.getUserId(),
                feed.getCreatedAt()
        );
    }
}
