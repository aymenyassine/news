package com.news.service;

import com.news.dto.PostDto;
import com.news.dto.PostRequest;
import com.news.model.Post;
import com.news.model.User;
import org.springframework.data.domain.Page;

/**
 * Contrat du service de gestion des publications utilisateurs.
 */
public interface IPostService {

    /**
     * Feed public — uniquement les posts PUBLISHED, avec pagination et filtre categorie.
     */
    Page<PostDto> getPublicFeed(int page, int pageSize, String category);

    /**
     * Detail d'un post (accessible uniquement si PUBLISHED).
     */
    PostDto getPostById(Long id);

    /**
     * Posts de l'utilisateur connecte (tous statuts).
     */
    Page<PostDto> getMyPosts(User user, int page, int pageSize);

    /**
     * Creation d'un post par un utilisateur authentifie.
     */
    PostDto createPost(PostRequest request, User author);

    /**
     * Modification d'un post.
     * USER : seulement ses propres posts. ADMIN : tous les posts.
     */
    PostDto updatePost(Long id, PostRequest request, User currentUser);

    /**
     * Suppression d'un post.
     * USER : suppression physique de ses posts.
     * ADMIN : passage en statut DELETED (restauration possible).
     */
    void deletePost(Long id, User currentUser);

    /**
     * Signalement d'un post — incremente le compteur de signalements.
     */
    void reportPost(Long id, User reporter);

    /**
     * Convertit une entite Post en PostDto.
     * Expose pour permettre la reutilisation dans AdminService.
     */
    PostDto toDto(Post post);
}
