package com.news.service.impl;

import com.news.dto.PostDto;
import com.news.dto.PostRequest;
import com.news.enums.PostStatus;
import com.news.enums.Role;
import com.news.exception.BadRequestException;
import com.news.exception.ForbiddenException;
import com.news.exception.ResourceNotFoundException;
import com.news.model.Post;
import com.news.model.User;
import com.news.repository.PostRepository;
import com.news.service.IPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation du service de gestion des publications utilisateurs.
 * RBAC : USER gere ses propres posts, ADMIN gere tous les posts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements IPostService {

    private final PostRepository postRepository;

    @Override
    public Page<PostDto> getPublicFeed(int page, int pageSize, String category) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Post> posts;

        if (category != null && !category.isBlank()) {
            posts = postRepository.findByStatusAndCategory(PostStatus.PUBLISHED, category, pageable);
        } else {
            posts = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);
        }

        return posts.map(this::toDto);
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = findPostOrThrow(id);
        if (post.getStatus() == PostStatus.DELETED) {
            throw new ResourceNotFoundException("Post non trouve");
        }
        return toDto(post);
    }

    @Override
    public Page<PostDto> getMyPosts(User user, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return postRepository.findByAuthor(user, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public PostDto createPost(PostRequest request, User author) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .author(author)
                .status(PostStatus.PUBLISHED)
                .reportCount(0)
                .build();

        Post saved = postRepository.save(post);
        log.info("Post cree par {} : id={}", author.getEmail(), saved.getId());
        return toDto(saved);
    }

    @Override
    @Transactional
    public PostDto updatePost(Long id, PostRequest request, User currentUser) {
        Post post = findPostOrThrow(id);
        checkOwnershipOrAdmin(post, currentUser);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setCategory(request.getCategory());

        Post saved = postRepository.save(post);
        log.info("Post {} modifie par {}", id, currentUser.getEmail());
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deletePost(Long id, User currentUser) {
        Post post = findPostOrThrow(id);

        if (currentUser.getRole() == Role.ADMIN) {
            // Admin : passage en DELETED (restauration possible)
            post.setStatus(PostStatus.DELETED);
            post.setDeletedAt(LocalDateTime.now());
            post.setDeletedByAdminId(currentUser.getId());
            postRepository.save(post);
            log.info("Post {} supprime (DELETED) par admin {}", id, currentUser.getEmail());
        } else {
            // User : seulement ses propres posts
            if (!post.getAuthor().getId().equals(currentUser.getId())) {
                throw new ForbiddenException("Vous ne pouvez supprimer que vos propres posts");
            }
            postRepository.delete(post);
            log.info("Post {} supprime par son auteur {}", id, currentUser.getEmail());
        }
    }

    @Override
    @Transactional
    public void reportPost(Long id, User reporter) {
        Post post = findPostOrThrow(id);
        if (post.getStatus() == PostStatus.DELETED) {
            throw new BadRequestException("Ce post n'est plus disponible");
        }
        post.setReportCount(post.getReportCount() + 1);
        postRepository.save(post);
        log.info("Post {} signale par {} (total: {})", id, reporter.getEmail(), post.getReportCount());
    }

    @Override
    public PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .category(post.getCategory())
                .status(post.getStatus())
                .reportCount(post.getReportCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(PostDto.AuthorDto.builder()
                        .id(post.getAuthor().getId())
                        .name(post.getAuthor().getName())
                        .avatarUrl(post.getAuthor().getAvatarUrl())
                        .build())
                .build();
    }

    // ===== Methodes privees =====

    private Post findPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post non trouve avec l'id : " + id));
    }

    private void checkOwnershipOrAdmin(Post post, User user) {
        if (user.getRole() != Role.ADMIN && !post.getAuthor().getId().equals(user.getId())) {
            throw new ForbiddenException("Vous n'avez pas les droits pour modifier ce post");
        }
    }
}
