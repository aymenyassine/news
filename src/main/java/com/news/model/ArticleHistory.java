package com.news.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entite ArticleHistory — historique des articles consultes par un utilisateur.
 * Limite aux 50 derniers articles.
 */
@Entity
@Table(name = "article_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String articleUrl;

    private String title;

    private String sourceName;

    private String urlToImage;

    @Builder.Default
    private LocalDateTime viewedAt = LocalDateTime.now();
}
