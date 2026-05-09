package com.news.service;

import com.news.dto.NewsApiResponse;

import java.util.List;

/**
 * Contrat du service proxy NewsAPI.org.
 */
public interface INewsApiService {

    /**
     * Top headlines selon pays et categorie, avec pagination.
     */
    NewsApiResponse getTopHeadlines(String country, String category, int page, int pageSize);

    /**
     * Recherche full-text via NewsAPI /everything.
     */
    NewsApiResponse searchEverything(String q, String from, String sortBy,
                                     String language, int page, int pageSize);

    /**
     * Sources disponibles filtrees par categorie, langue et pays.
     */
    Object getSources(String category, String language, String country);

    /**
     * Liste statique des categories disponibles.
     */
    List<String> getCategories();
}
