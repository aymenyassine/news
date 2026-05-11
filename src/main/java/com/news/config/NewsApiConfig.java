package com.news.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NewsApiConfig {

    @Value("${newsapi.base-url}")
    private String baseUrl;

    @Value("${newsapi.api-key}")
    private String apiKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

