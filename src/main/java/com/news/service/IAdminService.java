package com.news.service;

import com.news.dto.AdminStatsDto;
import com.news.dto.AdminUserDto;
import com.news.dto.PostDto;
import com.news.dto.PostStatusRequest;
import com.news.model.User;
import org.springframework.data.domain.Page;

/**
 * Contrat du service d'administration.
 *
 * Regles metier :
 * - Un ADMIN ne peut PAS se bannir lui-meme (400 Bad Request).
 * - Un ADMIN ne peut PAS bannir un autre compte ADMIN (400 Bad Request).
 * - Les tokens JWT actifs d'un compte banni sont rejetes immediatement.
 */
public interface IAdminService {

    // ===== Statistiques =====

    /**
     * Statistiques globales : utilisateurs, bannis, posts, signalements.
     */
    AdminStatsDto getStats();

    // ===== Gestion des comptes =====

    /**
     * Liste paginee de tous les comptes avec filtres optionnels (search, status).
     */
    Page<AdminUserDto> listUsers(int page, int size, String search, String status);

    /**
     * Profil complet d'un utilisateur.
     */
    AdminUserDto getUserById(Long id);

    /**
     * Bannit un compte utilisateur.
     * Leve BadRequestException si la cible est ADMIN ou si c'est un auto-ban.
     */
    AdminUserDto banUser(Long targetId, String reason, User adminUser);

    /**
     * Debannit un compte utilisateur.
     */
    AdminUserDto unbanUser(Long targetId, User adminUser);

    // ===== Gestion des publications =====

    /**
     * Liste paginee de tous les posts avec filtres optionnels (status, reported).
     */
    Page<PostDto> listPosts(int page, int size, String status, Boolean reported);

    /**
     * Suppression definitive d'un post.
     */
    void deletePost(Long postId, User adminUser);

    /**
     * Change le statut d'un post (PUBLISHED / DELETED).
     */
    PostDto updatePostStatus(Long postId, PostStatusRequest request, User adminUser);
}
