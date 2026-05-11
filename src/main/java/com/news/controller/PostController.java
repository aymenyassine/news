package com.news.controller;

import com.news.dto.PostDto;
import com.news.dto.PostRequest;
import com.news.model.User;
import com.news.service.IPostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Publications", description = "CRUD des posts utilisateurs")
public class PostController {

    private final IPostService postService;

    @GetMapping
    public ResponseEntity<Page<PostDto>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(postService.getPublicFeed(page, pageSize, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/my")
    ")
    public ResponseEntity<Page<PostDto>> getMyPosts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(postService.getMyPosts(user, page, pageSize));
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(postService.updatePost(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        postService.deletePost(id, user);
        return ResponseEntity.ok(Map.of("message", "Post supprime avec succes"));
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<Map<String, String>> reportPost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        postService.reportPost(id, user);
        return ResponseEntity.ok(Map.of("message", "Post signale avec succes"));
    }
}


