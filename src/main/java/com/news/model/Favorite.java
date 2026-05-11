package com.news.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "article_url"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "article_url", nullable = false)
    private String articleUrl;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String urlToImage;

    private String sourceName;

    @Builder.Default
    private LocalDateTime savedAt = LocalDateTime.now();
}

