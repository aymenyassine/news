package com.news.service;

import com.news.dto.ArticleHistoryDto;
import com.news.dto.UserProfileDto;
import com.news.model.User;

import java.util.List;

/**
 * Contrat du service de gestion du profil utilisateur.
 */
public interface IUserService {

    /**
     * Retourne le profil de l'utilisateur connecte.
     */
    UserProfileDto getProfile(User user);

    /**
     * Met a jour le profil (nom, avatarUrl, preferences).
     */
    UserProfileDto updateProfile(User user, UserProfileDto dto);

    /**
     * Retourne les 50 derniers articles consultes par l'utilisateur.
     */
    List<ArticleHistoryDto> getHistory(User user);

    /**
     * Enregistre un article consulte dans l'historique.
     * Limite a 50 entrees — supprime les plus anciennes si necessaire.
     */
    ArticleHistoryDto addToHistory(User user, ArticleHistoryDto dto);
}
