package com.news.service.impl;

import com.news.dto.AdminStatsDto;
import com.news.dto.AdminUserDto;
import com.news.dto.PostDto;
import com.news.dto.PostStatusRequest;
import com.news.enums.PostStatus;
import com.news.enums.Role;
import com.news.exception.BadRequestException;
import com.news.exception.ResourceNotFoundException;
import com.news.model.Post;
import com.news.model.User;
import com.news.repository.PostRepository;
import com.news.repository.RefreshTokenRepository;
import com.news.repository.UserRepository;
import com.news.service.IAdminService;
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
 * Implementation du service d'administration.
 * Toutes les actions admin sont loguees avec userId et timestamp.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminServiceImpl implements IAdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final IPostService postService;

    // ===== Statistiques =====

    @Override
    public AdminStatsDto getStats() {
        return AdminStatsDto.builder()
                .totalUsers(userRepository.count())
                .bannedUsers(userRepository.countByBannedTrue())
                .totalPosts(postRepository.count())
                .reportedPosts(postRepository.countByReportCountGreaterThan(0))
                .publishedPosts(postRepository.countByStatus(PostStatus.PUBLISHED))
                .deletedPosts(postRepository.countByStatus(PostStatus.DELETED))
                .build();
    }

    // ===== Gestion des comptes =====

    @Override
    public Page<AdminUserDto> listUsers(int page, int size, String search, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAllWithFilters(search, status, pageable)
                .map(this::toAdminUserDto);
    }

    @Override
    public AdminUserDto getUserById(Long id) {
        return toAdminUserDto(findUserOrThrow(id));
    }

    @Override
    @Transactional
    public AdminUserDto banUser(Long targetId, String reason, User adminUser) {
        User target = findUserOrThrow(targetId);

        // Regle : auto-ban interdit
        if (target.getId().equals(adminUser.getId())) {
            throw new BadRequestException("Un administrateur ne peut pas se bannir lui-meme");
        }

        // Regle : ban d'un autre ADMIN interdit
        if (target.getRole() == Role.ADMIN) {
            throw new BadRequestException("Il est impossible de bannir un compte administrateur");
        }

        if (target.isBanned()) {
            throw new BadRequestException("Ce compte est deja banni");
        }

        target.setBanned(true);
        target.setBanReason(reason);
        target.setBannedAt(LocalDateTime.now());
        userRepository.save(target);

        // Revoquer tous les refresh tokens — les access tokens seront rejetes par JwtAuthFilter
        refreshTokenRepository.revokeAllByUser(target);

        log.info("[ADMIN ACTION] Ban : adminId={} a banni userId={} ({}), raison={}",
                adminUser.getId(), target.getId(), target.getEmail(), reason);

        return toAdminUserDto(target);
    }

    @Override
    @Transactional
    public AdminUserDto unbanUser(Long targetId, User adminUser) {
        User target = findUserOrThrow(targetId);

        if (!target.isBanned()) {
            throw new BadRequestException("Ce compte n'est pas banni");
        }

        target.setBanned(false);
        target.setBanReason(null);
        target.setBannedAt(null);
        userRepository.save(target);

        log.info("[ADMIN ACTION] Unban : adminId={} a debanni userId={} ({})",
                adminUser.getId(), target.getId(), target.getEmail());

        return toAdminUserDto(target);
    }

    // ===== Gestion des publications =====

    @Override
    public Page<PostDto> listPosts(int page, int size, String status, Boolean reported) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PostStatus postStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                postStatus = PostStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Statut invalide : " + status);
            }
        }
        return postRepository.findAllWithFilters(postStatus, reported, pageable)
                .map(postService::toDto);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, User adminUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post non trouve : " + postId));

        postRepository.delete(post);
        log.info("[ADMIN ACTION] Suppression definitive : adminId={} a supprime postId={}",
                adminUser.getId(), postId);
    }

    @Override
    @Transactional
    public PostDto updatePostStatus(Long postId, PostStatusRequest request, User adminUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post non trouve : " + postId));

        PostStatus oldStatus = post.getStatus();
        post.setStatus(request.getStatus());

        if (request.getStatus() == PostStatus.DELETED) {
            post.setDeletedAt(LocalDateTime.now());
            post.setDeletedByAdminId(adminUser.getId());
        } else {
            post.setDeletedAt(null);
            post.setDeletedByAdminId(null);
        }

        Post saved = postRepository.save(post);
        log.info("[ADMIN ACTION] Statut post : adminId={} a change postId={} de {} a {}",
                adminUser.getId(), postId, oldStatus, request.getStatus());

        return postService.toDto(saved);
    }

    // ===== Methodes privees =====

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve : " + id));
    }

    private AdminUserDto toAdminUserDto(User user) {
        return AdminUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .banned(user.isBanned())
                .banReason(user.getBanReason())
                .bannedAt(user.getBannedAt())
                .createdAt(user.getCreatedAt())
                .postCount(postRepository.countByAuthor(user))
                .build();
    }
}
