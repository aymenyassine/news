package com.news.dto;

import com.news.enums.PostStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostStatusRequest {

    @NotNull(message = "Le statut est obligatoire")
    private PostStatus status;
}
