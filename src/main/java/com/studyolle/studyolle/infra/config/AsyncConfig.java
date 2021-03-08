package com.studyolle.studyolle.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors); // 기본적으로 생성해두는 쓰레드 풀 사이즈
        executor.setMaxPoolSize(processors * 2); // 쓰레드가 전부 사용중이고, Queue에 대기중인 요청도 가득 찬상태에서 요청이오면 최대한도내에서 쓰레드 생성해서 일 처리시킴
        executor.setQueueCapacity(50); // 기본 생성된 쓰레드들이 전부 사용될경우 추가 요청이 오면 설정값만큼 대기열을 세워둠. 기본값 = Integer.MAX
        executor.setKeepAliveSeconds(60); // 기본생성된 쓰레드 외 추가적으로 생성된 쓰레드의 유지시간
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        log.info("processors count {}", processors);
        return executor;
    }
}
