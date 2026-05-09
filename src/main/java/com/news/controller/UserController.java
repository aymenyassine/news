package com.news.controller;

import com.news.dto.ArticleHistoryDto;
import com.news.dto.UserProfileDto;
import com.news.model.User;
import com.news.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller du profil utilisateur et de l'historique de consultation.
 * Acces : USER + ADMIN authentifies.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Profil et historique de l'utilisateur connecte")
public class UserController {

    private final IUserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Mon profil", description = "Retourne le profil de l'utilisateur connecte")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "Mettre a jour le profil", description = "Modifie nom, avatarUrl, preferences")
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfileDto dto) {
        return ResponseEntity.ok(userService.updateProfile(user, dto));
    }

    @GetMapping("/history")
    @Operation(summary = "Historique de consultation", description = "50 derniers articles consultes")
    public ResponseEntity<List<ArticleHistoryDto>> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getHistory(user));
    }

    @PostMapping("/history")
    @Operation(summary = "Enregistrer un article consulte")
    public ResponseEntity<ArticleHistoryDto> addToHistory(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ArticleHistoryDto dto) {
        return ResponseEntity.ok(userService.addToHistory(user, dto));
    }
}
