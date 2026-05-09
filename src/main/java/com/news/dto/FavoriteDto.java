package com.news.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDto {

    private Long id;

    @NotBlank(message = "L'URL de l'article est obligatoire")
    private String articleUrl;

    private String title;
    private String description;
    private String urlToImage;
    private String sourceName;
    private LocalDateTime savedAt;
}
