package com.news.controller;

import com.news.dto.NewsApiResponse;
import com.news.service.INewsApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Tag(name = "Actualites", description = "Proxy NewsAPI.org — headlines, recherche, sources, categories")
public class NewsController {

    private final INewsApiService newsApiService;

    @GetMapping("/headlines")
    public ResponseEntity<NewsApiResponse> getHeadlines(
            @RequestParam(defaultValue = "fr") String country,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(newsApiService.getTopHeadlines(country, category, page, pageSize));
    }

    @GetMapping("/search")
    public ResponseEntity<NewsApiResponse> search(
            @RequestParam String q,
            @RequestParam(required = false) String from,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "fr") String language,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(newsApiService.searchEverything(q, from, sortBy, language, page, pageSize));
    }

    @GetMapping("/sources")
    public ResponseEntity<Object> getSources(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String country) {
        return ResponseEntity.ok(newsApiService.getSources(category, language, country));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(newsApiService.getCategories());
    }
}


