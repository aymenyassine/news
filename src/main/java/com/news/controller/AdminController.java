package com.news.controller;

import com.news.dto.AdminStatsDto;
import com.news.dto.AdminUserDto;
import com.news.dto.BanRequest;
import com.news.dto.PostDto;
import com.news.dto.PostStatusRequest;
import com.news.model.User;
import com.news.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller d'administration — ADMIN uniquement.
 * Tous les endpoints sont proteges par @PreAuthorize("hasRole('ADMIN')")
 * ET par la regle SecurityConfig .requestMatchers("/api/admin/**").hasRole("ADMIN").
 * Double protection : URL-level + method-level.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administration", description = "Panel admin — gestion comptes et publications (ADMIN uniquement)")
public class AdminController {

    private final IAdminService adminService;

    // ===== Statistiques =====

    @GetMapping("/stats")
    @Operation(summary = "Statistiques globales",
               description = "{ totalUsers, bannedUsers, totalPosts, reportedPosts }")
    public ResponseEntity<AdminStatsDto> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // ===== Gestion des comptes =====

    @GetMapping("/users")
    @Operation(summary = "Liste des comptes",
               description = "Tableau pagine : email, nom, role, statut, date inscription")
    public ResponseEntity<Page<AdminUserDto>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.listUsers(page, size, search, status));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Profil complet d'un utilisateur")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}/ban")
    @Operation(summary = "Bannir un compte",
               description = "Passe banned=true. Tokens actifs rejetes immediatement. " +
                             "400 si ADMIN ou auto-ban.")
    public ResponseEntity<AdminUserDto> banUser(
            @PathVariable Long id,
            @RequestBody(required = false) BanRequest banRequest,
            @AuthenticationPrincipal User adminUser) {
        String reason = banRequest != null ? banRequest.getReason() : null;
        return ResponseEntity.ok(adminService.banUser(id, reason, adminUser));
    }

    @PutMapping("/users/{id}/unban")
    @Operation(summary = "Debannir un compte", description = "Passe banned=false")
    public ResponseEntity<AdminUserDto> unbanUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User adminUser) {
        return ResponseEntity.ok(adminService.unbanUser(id, adminUser));
    }

    // ===== Gestion des publications =====

    @GetMapping("/posts")
    @Operation(summary = "Tous les posts",
               description = "Tableau : titre, auteur, date, statut, signalements. " +
                             "Filtres : status (PUBLISHED/DELETED), reported (true/false)")
    public ResponseEntity<Page<PostDto>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean reported) {
        return ResponseEntity.ok(adminService.listPosts(page, size, status, reported));
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Suppression definitive d'un post")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User adminUser) {
        adminService.deletePost(id, adminUser);
        return ResponseEntity.ok(Map.of("message", "Post supprime definitivement"));
    }

    @PutMapping("/posts/{id}/status")
    @Operation(summary = "Changer le statut d'un post",
               description = "Body: { status: 'PUBLISHED' | 'DELETED' }")
    public ResponseEntity<PostDto> updatePostStatus(
            @PathVariable Long id,
            @Valid @RequestBody PostStatusRequest request,
            @AuthenticationPrincipal User adminUser) {
        return ResponseEntity.ok(adminService.updatePostStatus(id, request, adminUser));
    }
}
