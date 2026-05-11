package com.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(min = 1, max = 1000, message = "Le commentaire doit contenir entre 1 et 1000 caracteres")
    private String content;
}
