package com.zero9platform.domain.activity_feed.event;

import com.zero9platform.domain.activity_feed.service.ActivityFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FeedEventListener {

    private final ActivityFeedService activityFeedService;

    @Async("FEED_TASK_EXECUTOR")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 메인 실행 후 실행해라
    public void handleFeedEvent(FeedCreateEvent event) {
        activityFeedService.feedUpsert(
                event.getType(),
                event.getTargetId(),
                event.getTitle(),
                event.getUserId(),
                event.getArgs()
        );
    }
}
