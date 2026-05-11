package com.news.service;

import com.news.dto.CommentDto;
import com.news.dto.CommentRequest;
import com.news.model.User;
import org.springframework.data.domain.Page;

/**
 * Contrat du service de gestion des commentaires.
 */
public interface ICommentService {

    /**
     * Recupere les commentaires d'un post (public, pagine).
     */
    Page<CommentDto> getCommentsByPost(Long postId, int page, int pageSize);

    /**
     * Ajoute un commentaire sur un post.
     */
    CommentDto addComment(Long postId, CommentRequest request, User author);

    /**
     * Modifie un commentaire (auteur uniquement).
     */
    CommentDto updateComment(Long commentId, CommentRequest request, User currentUser);

    /**
     * Supprime un commentaire (auteur ou ADMIN).
     */
    void deleteComment(Long commentId, User currentUser);
}
