package com.kijinkai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 이벤트 처리용 비동기 실행자 설정
     */
    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);        // 기본 스레드 수
        executor.setMaxPoolSize(10);        // 최대 스레드 수
        executor.setQueueCapacity(25);      // 큐 용량
        executor.setKeepAliveSeconds(60);   // 스레드 유지 시간
        executor.setThreadNamePrefix("UserEvent-"); // 스레드 이름 접두사
        executor.setWaitForTasksToCompleteOnShutdown(true); // 종료 시 대기
        executor.setAwaitTerminationSeconds(60); // 종료 대기 시간
        executor.initialize();
        return executor;
    }
}
