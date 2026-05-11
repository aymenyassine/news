package com.news.service;

import com.news.dto.NewsApiResponse;

import java.util.List;

public interface INewsApiService {

    NewsApiResponse getTopHeadlines(String country, String category, int page, int pageSize);

    NewsApiResponse searchEverything(String q, String from, String sortBy,
                                     String language, int page, int pageSize);

    Object getSources(String category, String language, String country);

    List<String> getCategories();
}

