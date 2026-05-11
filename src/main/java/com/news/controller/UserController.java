package com.news.controller;

import com.news.dto.ArticleHistoryDto;
import com.news.dto.UserProfileDto;
import com.news.model.User;
import com.news.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Profil et historique de l'utilisateur connecte")
public class UserController {

    private final IUserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfileDto dto) {
        return ResponseEntity.ok(userService.updateProfile(user, dto));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ArticleHistoryDto>> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getHistory(user));
    }

    @PostMapping("/history")
    public ResponseEntity<ArticleHistoryDto> addToHistory(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ArticleHistoryDto dto) {
        return ResponseEntity.ok(userService.addToHistory(user, dto));
    }
}


