package com.knowledge.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.Executor;

/**
 * AI相关配置
 * 配置OpenAI客户端和异步任务执行器
 */
@Configuration
@EnableAsync
public class AIConfig {

    @Value("${ai.model.api.url:}")
    private String aiModelApiUrl;

    @Value("${ai.model.api.key:}")
    private String aiModelApiKey;

    /**
     * 配置AI任务异步执行器
     * @return 线程池执行器
     */
    @Bean("aiTaskExecutor")
    public Executor aiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("AI-Task-");
        executor.initialize();
        return executor;
    }

    /**
     * 配置RestTemplate
     * @param builder RestTemplateBuilder
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }
}
