package com.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.news.enums.PostStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String category;
    private PostStatus status;
    private int reportCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AuthorDto author;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorDto {
        private Long id;
        private String name;
        private String avatarUrl;
    }
}
