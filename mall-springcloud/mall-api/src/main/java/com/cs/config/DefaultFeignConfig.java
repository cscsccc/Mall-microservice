package com.cs.config;

import com.cs.fallback.GoodClientFallBack;
import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public GoodClientFallBack goodClientFallBack() {
        return new GoodClientFallBack();
    }
}
