package com.news.service;

import com.news.dto.AdminStatsDto;
import com.news.dto.AdminUserDto;
import com.news.dto.PostDto;
import com.news.dto.PostStatusRequest;
import com.news.model.User;
import org.springframework.data.domain.Page;

public interface IAdminService {

    // ===== Statistiques =====

    AdminStatsDto getStats();

    // ===== Gestion des comptes =====

    Page<AdminUserDto> listUsers(int page, int size, String search, String status);

    AdminUserDto getUserById(Long id);

    AdminUserDto banUser(Long targetId, String reason, User adminUser);

    AdminUserDto unbanUser(Long targetId, User adminUser);

    // ===== Gestion des publications =====

    Page<PostDto> listPosts(int page, int size, String status, Boolean reported);

    void deletePost(Long postId, User adminUser);

    PostDto updatePostStatus(Long postId, PostStatusRequest request, User adminUser);
}

