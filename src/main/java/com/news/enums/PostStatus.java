package com.news.enums;

/**
 * Statut d'un post utilisateur.
 * PUBLISHED : visible dans le feed public.
 * DELETED   : supprime par admin, masque du feed (non efface physiquement).
 */
public enum PostStatus {
    PUBLISHED,
    DELETED
}
