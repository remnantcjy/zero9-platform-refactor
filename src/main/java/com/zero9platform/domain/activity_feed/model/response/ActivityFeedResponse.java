package com.zero9platform.domain.activity_feed.model.response;

import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class ActivityFeedResponse {
    private final Long id;
    private final String type;
    private final String message;
    private final Long productPostId;
    private final String createdAt;

    public ActivityFeedResponse(Long id, String type, String message, Long productPostId, String createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.productPostId = productPostId;
        this.createdAt = createdAt;
    }

    public static ActivityFeedResponse from(ActivityFeed feed) {
        return new ActivityFeedResponse(
                feed.getId(),
                feed.getType(),
                feed.getMessage(),
                feed.getProductPostId(),
                feed.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}