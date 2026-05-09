package com.news.controller;

import com.news.dto.FavoriteDto;
import com.news.model.User;
import com.news.service.IFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller des favoris — articles NewsAPI sauvegardes.
 * Acces : USER + ADMIN authentifies.
 */
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favoris", description = "Gestion des articles NewsAPI sauvegardes")
public class FavoriteController {

    private final IFavoriteService favoriteService;

    @GetMapping
    @Operation(summary = "Mes favoris", description = "Liste des articles sauvegardes par l'utilisateur connecte")
    public ResponseEntity<List<FavoriteDto>> getFavorites(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(favoriteService.getFavorites(user));
    }

    @PostMapping
    @Operation(summary = "Ajouter un favori", description = "Sauvegarde un article NewsAPI")
    public ResponseEntity<FavoriteDto> addFavorite(
            @Valid @RequestBody FavoriteDto request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.addFavorite(request, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un favori")
    public ResponseEntity<Map<String, String>> deleteFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        favoriteService.deleteFavorite(id, user);
        return ResponseEntity.ok(Map.of("message", "Favori supprime avec succes"));
    }

    @GetMapping("/check")
    @Operation(summary = "Verifier si un article est en favori")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @RequestParam String url,
            @AuthenticationPrincipal User user) {
        boolean isFavorite = favoriteService.isFavorite(url, user);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }
}
