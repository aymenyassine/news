package com.news.service.impl;

import com.news.config.CacheConfig;
import com.news.config.NewsApiConfig;
import com.news.dto.NewsApiResponse;
import com.news.service.INewsApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsApiServiceImpl implements INewsApiService {

    private final RestTemplate restTemplate;
    private final NewsApiConfig newsApiConfig;

    private static final List<String> VALID_CATEGORIES =
            List.of("business", "entertainment", "health", "science", "sports", "technology");

    @Override
    @Cacheable(value = CacheConfig.NEWS_HEADLINES_CACHE,
               key = "#country + '_' + #category + '_' + #page + '_' + #pageSize")
    public NewsApiResponse getTopHeadlines(String country, String category, int page, int pageSize) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(newsApiConfig.getBaseUrl() + "/top-headlines")
                .queryParam("apiKey", newsApiConfig.getApiKey())
                .queryParam("pageSize", Math.min(pageSize, 100))
                .queryParam("page", page);

        if (country != null && !country.isBlank()) {
            builder.queryParam("country", country);
        }
        if (category != null && !category.isBlank() && VALID_CATEGORIES.contains(category)) {
            builder.queryParam("category", category);
        }

        return callNewsApi(builder.toUriString());
    }

    @Override
    @Cacheable(value = CacheConfig.NEWS_SEARCH_CACHE,
               key = "#q + '_' + #from + '_' + #sortBy + '_' + #language + '_' + #page + '_' + #pageSize")
    public NewsApiResponse searchEverything(String q, String from, String sortBy,
                                             String language, int page, int pageSize) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le parametre de recherche 'q' est obligatoire");
        }

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(newsApiConfig.getBaseUrl() + "/everything")
                .queryParam("apiKey", newsApiConfig.getApiKey())
                .queryParam("q", q)
                .queryParam("pageSize", Math.min(pageSize, 100))
                .queryParam("page", page);

        if (from != null && !from.isBlank())         builder.queryParam("from", from);
        if (sortBy != null && !sortBy.isBlank())      builder.queryParam("sortBy", sortBy);
        if (language != null && !language.isBlank())  builder.queryParam("language", language);

        return callNewsApi(builder.toUriString());
    }

    @Override
    @Cacheable(value = CacheConfig.NEWS_SOURCES_CACHE,
               key = "#category + '_' + #language + '_' + #country")
    public Object getSources(String category, String language, String country) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(newsApiConfig.getBaseUrl() + "/top-headlines/sources")
                .queryParam("apiKey", newsApiConfig.getApiKey());

        if (category != null && !category.isBlank()) builder.queryParam("category", category);
        if (language != null && !language.isBlank())  builder.queryParam("language", language);
        if (country != null && !country.isBlank())    builder.queryParam("country", country);

        return callNewsApiRaw(builder.toUriString());
    }

    @Override
    public List<String> getCategories() {
        return VALID_CATEGORIES;
    }

    // ===== Methodes privees =====

    private NewsApiResponse callNewsApi(String url) {
        try {
            log.debug("Appel NewsAPI : {}", url.replaceAll("apiKey=[^&]+", "apiKey=***"));
            return restTemplate.getForObject(url, NewsApiResponse.class);
        } catch (HttpClientErrorException e) {
            return handleNewsApiError(e, null);
        } catch (Exception e) {
            log.error("Erreur appel NewsAPI : {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Service NewsAPI temporairement indisponible");
        }
    }

    private Object callNewsApiRaw(String url) {
        try {
            return restTemplate.getForObject(url, Object.class);
        } catch (HttpClientErrorException e) {
            return handleNewsApiError(e, null);
        } catch (Exception e) {
            log.error("Erreur appel NewsAPI sources : {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Service NewsAPI temporairement indisponible");
        }
    }

    private NewsApiResponse handleNewsApiError(HttpClientErrorException e, NewsApiResponse cached) {
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            log.error("Cle NewsAPI invalide — 401 Unauthorized");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Cle API NewsAPI invalide");
        }
        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            log.warn("Quota NewsAPI depasse — 429 Too Many Requests");
            if (cached != null) {
                log.info("Retour du cache suite au quota depasse");
                return cached;
            }
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Quota NewsAPI depasse. Reessayez plus tard.");
        }
        log.error("Erreur NewsAPI {} : {}", e.getStatusCode(), e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Erreur NewsAPI : " + e.getMessage());
    }
}

