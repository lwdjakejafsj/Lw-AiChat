package io.luowei.aichat.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AopConfig {

    @Bean
    public IRateLimiter rateLimiter() {
        return new IRateLimiter();
    }

}
