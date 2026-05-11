package com.news.controller;

import com.news.dto.FavoriteDto;
import com.news.model.User;
import com.news.service.IFavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favoris", description = "Gestion des articles NewsAPI sauvegardes")
public class FavoriteController {

    private final IFavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteDto>> getFavorites(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(favoriteService.getFavorites(user));
    }

    @PostMapping
    public ResponseEntity<FavoriteDto> addFavorite(
            @Valid @RequestBody FavoriteDto request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.addFavorite(request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        favoriteService.deleteFavorite(id, user);
        return ResponseEntity.ok(Map.of("message", "Favori supprime avec succes"));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @RequestParam String url,
            @AuthenticationPrincipal User user) {
        boolean isFavorite = favoriteService.isFavorite(url, user);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }
}


