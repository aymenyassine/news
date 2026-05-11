package com.news.controller;

import com.news.dto.AdminStatsDto;
import com.news.dto.AdminUserDto;
import com.news.dto.BanRequest;
import com.news.dto.PostDto;
import com.news.dto.PostStatusRequest;
import com.news.model.User;
import com.news.service.IAdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administration", description = "Panel admin — gestion comptes et publications (ADMIN uniquement)")
public class AdminController {

    private final IAdminService adminService;

    // ===== Statistiques =====

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // ===== Gestion des comptes =====

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserDto>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.listUsers(page, size, search, status));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<AdminUserDto> banUser(
            @PathVariable Long id,
            @RequestBody(required = false) BanRequest banRequest,
            @AuthenticationPrincipal User adminUser) {
        String reason = banRequest != null ? banRequest.getReason() : null;
        return ResponseEntity.ok(adminService.banUser(id, reason, adminUser));
    }

    @PutMapping("/users/{id}/unban")
    public ResponseEntity<AdminUserDto> unbanUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User adminUser) {
        return ResponseEntity.ok(adminService.unbanUser(id, adminUser));
    }

    // ===== Gestion des publications =====

    @GetMapping("/posts")
    , reported (true/false)")
    public ResponseEntity<Page<PostDto>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean reported) {
        return ResponseEntity.ok(adminService.listPosts(page, size, status, reported));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User adminUser) {
        adminService.deletePost(id, adminUser);
        return ResponseEntity.ok(Map.of("message", "Post supprime definitivement"));
    }

    @PutMapping("/posts/{id}/status")
    public ResponseEntity<PostDto> updatePostStatus(
            @PathVariable Long id,
            @Valid @RequestBody PostStatusRequest request,
            @AuthenticationPrincipal User adminUser) {
        return ResponseEntity.ok(adminService.updatePostStatus(id, request, adminUser));
    }
}


