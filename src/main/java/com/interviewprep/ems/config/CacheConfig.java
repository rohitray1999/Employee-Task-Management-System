package com.interviewprep.ems.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    // Simple in-memory cache manager - swap for Redis/Caffeine in production.
    // Named caches used across the service layer:
    //  - "departments": individual department lookups by id
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("departments");
    }
}
