package com.example.ld.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author HeYiyu
 * @date 2023/2/8
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(16);
    }

    @Bean
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(16);
    }
}
