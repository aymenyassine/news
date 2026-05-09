package com.news.service;

import com.news.dto.FavoriteDto;
import com.news.model.User;

import java.util.List;

/**
 * Contrat du service de gestion des favoris.
 */
public interface IFavoriteService {

    /**
     * Retourne tous les favoris de l'utilisateur, tries par date de sauvegarde.
     */
    List<FavoriteDto> getFavorites(User user);

    /**
     * Ajoute un article NewsAPI aux favoris.
     * Leve BadRequestException si l'article est deja en favori.
     */
    FavoriteDto addFavorite(FavoriteDto request, User user);

    /**
     * Supprime un favori par son ID.
     * Verifie que le favori appartient bien a l'utilisateur.
     */
    void deleteFavorite(Long id, User user);

    /**
     * Verifie si un article est deja en favori pour l'utilisateur.
     */
    boolean isFavorite(String articleUrl, User user);
}
