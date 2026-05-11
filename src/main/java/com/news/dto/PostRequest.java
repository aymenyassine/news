package com.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 255, message = "Le titre doit contenir entre 3 et 255 caracteres")
    private String title;

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(min = 3, message = "Le contenu doit contenir au moins 3 caracteres")
    private String content;

    private String imageUrl;

    private String category;
}
