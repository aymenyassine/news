package com.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Reponse paginee de NewsAPI.org.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsApiResponse {

    private String status;
    private int totalResults;
    private List<ArticleDto> articles;
}
