package com.news.service.impl;

import com.news.dto.FavoriteDto;
import com.news.exception.BadRequestException;
import com.news.exception.ResourceNotFoundException;
import com.news.model.Favorite;
import com.news.model.User;
import com.news.repository.FavoriteRepository;
import com.news.service.IFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements IFavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Override
    public List<FavoriteDto> getFavorites(User user) {
        return favoriteRepository.findByUserOrderBySavedAtDesc(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public FavoriteDto addFavorite(FavoriteDto request, User user) {
        if (favoriteRepository.existsByUserAndArticleUrl(user, request.getArticleUrl())) {
            throw new BadRequestException("Cet article est deja dans vos favoris");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .articleUrl(request.getArticleUrl())
                .title(request.getTitle())
                .description(request.getDescription())
                .urlToImage(request.getUrlToImage())
                .sourceName(request.getSourceName())
                .savedAt(LocalDateTime.now())
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        log.info("Favori ajoute par {} : {}", user.getEmail(), request.getArticleUrl());
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteFavorite(Long id, User user) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favori non trouve"));

        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Favori non trouve");
        }

        favoriteRepository.delete(favorite);
        log.info("Favori {} supprime par {}", id, user.getEmail());
    }

    @Override
    public boolean isFavorite(String articleUrl, User user) {
        return favoriteRepository.existsByUserAndArticleUrl(user, articleUrl);
    }

    // ===== Methodes privees =====

    private FavoriteDto toDto(Favorite favorite) {
        return FavoriteDto.builder()
                .id(favorite.getId())
                .articleUrl(favorite.getArticleUrl())
                .title(favorite.getTitle())
                .description(favorite.getDescription())
                .urlToImage(favorite.getUrlToImage())
                .sourceName(favorite.getSourceName())
                .savedAt(favorite.getSavedAt())
                .build();
    }
}

