package com.zero9platform.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 피드 전용 스레드 풀 설정
     * @Async("feedTaskExecutor")로 지정하여 사용합니다.
     */
    @Bean(name = "FEED_TASK_EXECUTOR")
    public Executor feedTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 스레드 개수
        executor.setCorePoolSize(10);

        // 최대 스레드 개수
        executor.setMaxPoolSize(20);

        // 대기 큐 용량
        executor.setQueueCapacity(100);

        // 로그 접두사
        executor.setThreadNamePrefix("Feed-Async-");

        executor.initialize();
        return executor;
    }
}
