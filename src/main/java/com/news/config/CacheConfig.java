package com.news.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${newsapi.cache-ttl-minutes:15}")
    private int cacheTtlMinutes;

    public static final String NEWS_HEADLINES_CACHE = "newsHeadlines";
    public static final String NEWS_SEARCH_CACHE    = "newsSearch";
    public static final String NEWS_SOURCES_CACHE   = "newsSources";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                NEWS_HEADLINES_CACHE,
                NEWS_SEARCH_CACHE,
                NEWS_SOURCES_CACHE
        );
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(cacheTtlMinutes, TimeUnit.MINUTES)
                .recordStats());
        return manager;
    }
}

