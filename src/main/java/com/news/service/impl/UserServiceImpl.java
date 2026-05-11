package com.news.service.impl;

import com.news.dto.ArticleHistoryDto;
import com.news.dto.UserProfileDto;
import com.news.model.ArticleHistory;
import com.news.model.User;
import com.news.repository.ArticleHistoryRepository;
import com.news.repository.UserRepository;
import com.news.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

    private static final int MAX_HISTORY_SIZE = 50;

    private final UserRepository userRepository;
    private final ArticleHistoryRepository articleHistoryRepository;

    @Override
    public UserProfileDto getProfile(User user) {
        return UserProfileDto.builder()
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .preferences(user.getPreferences())
                .build();
    }

    @Override
    @Transactional
    public UserProfileDto updateProfile(User user, UserProfileDto dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getPreferences() != null) {
            user.setPreferences(dto.getPreferences());
        }

        User saved = userRepository.save(user);
        log.info("Profil mis a jour : {}", user.getEmail());
        return getProfile(saved);
    }

    @Override
    public List<ArticleHistoryDto> getHistory(User user) {
        return articleHistoryRepository
                .findByUserOrderByViewedAtDesc(user, PageRequest.of(0, MAX_HISTORY_SIZE))
                .stream()
                .map(this::toHistoryDto)
                .toList();
    }

    @Override
    @Transactional
    public ArticleHistoryDto addToHistory(User user, ArticleHistoryDto dto) {
        // Eviter les doublons consecutifs
        List<ArticleHistory> recent = articleHistoryRepository
                .findByUserOrderByViewedAtDesc(user, PageRequest.of(0, 1));
        if (!recent.isEmpty() && recent.get(0).getArticleUrl().equals(dto.getArticleUrl())) {
            return toHistoryDto(recent.get(0));
        }

        ArticleHistory history = ArticleHistory.builder()
                .user(user)
                .articleUrl(dto.getArticleUrl())
                .title(dto.getTitle())
                .sourceName(dto.getSourceName())
                .urlToImage(dto.getUrlToImage())
                .viewedAt(LocalDateTime.now())
                .build();

        ArticleHistory saved = articleHistoryRepository.save(history);

        // Nettoyage si depassement de la limite
        long count = articleHistoryRepository.countByUser(user);
        if (count > MAX_HISTORY_SIZE) {
            articleHistoryRepository.deleteOldestByUser(user, (int) (count - MAX_HISTORY_SIZE));
        }

        return toHistoryDto(saved);
    }

    // ===== Methodes privees =====

    private ArticleHistoryDto toHistoryDto(ArticleHistory h) {
        return ArticleHistoryDto.builder()
                .id(h.getId())
                .articleUrl(h.getArticleUrl())
                .title(h.getTitle())
                .sourceName(h.getSourceName())
                .urlToImage(h.getUrlToImage())
                .viewedAt(h.getViewedAt())
                .build();
    }
}

