package com.news.service.impl;

import com.news.dto.CommentDto;
import com.news.dto.CommentRequest;
import com.news.enums.PostStatus;
import com.news.enums.Role;
import com.news.exception.BadRequestException;
import com.news.exception.ForbiddenException;
import com.news.exception.ResourceNotFoundException;
import com.news.model.Comment;
import com.news.model.Post;
import com.news.model.User;
import com.news.repository.CommentRepository;
import com.news.repository.PostRepository;
import com.news.service.ICommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation du service de gestion des commentaires.
 * - Lecture publique (posts PUBLISHED uniquement).
 * - Ecriture reservee aux utilisateurs authentifies.
 * - Modification : auteur uniquement.
 * - Suppression : auteur ou ADMIN.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements ICommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public Page<CommentDto> getCommentsByPost(Long postId, int page, int pageSize) {
        Post post = findPublishedPostOrThrow(postId);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").ascending());
        return commentRepository.findByPost(post, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long postId, CommentRequest request, User author) {
        Post post = findPublishedPostOrThrow(postId);

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(request.getContent())
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Commentaire ajoute par {} sur le post {}", author.getEmail(), postId);
        return toDto(saved);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long commentId, CommentRequest request, User currentUser) {
        Comment comment = findCommentOrThrow(commentId);

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Vous ne pouvez modifier que vos propres commentaires");
        }

        comment.setContent(request.getContent());
        Comment saved = commentRepository.save(comment);
        log.info("Commentaire {} modifie par {}", commentId, currentUser.getEmail());
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = findCommentOrThrow(commentId);

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException("Vous n'avez pas les droits pour supprimer ce commentaire");
        }

        commentRepository.delete(comment);
        log.info("Commentaire {} supprime par {}", commentId, currentUser.getEmail());
    }

    // ===== Methodes privees =====

    private Post findPublishedPostOrThrow(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post non trouve avec l'id : " + postId));
        if (post.getStatus() == PostStatus.DELETED) {
            throw new BadRequestException("Ce post n'est plus disponible");
        }
        return post;
    }

    private Comment findCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire non trouve avec l'id : " + commentId));
    }

    private CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .author(CommentDto.AuthorDto.builder()
                        .id(comment.getAuthor().getId())
                        .name(comment.getAuthor().getName())
                        .avatarUrl(comment.getAuthor().getAvatarUrl())
                        .build())
                .build();
    }
}
