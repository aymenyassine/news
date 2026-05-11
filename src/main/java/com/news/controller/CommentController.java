package com.news.controller;

import com.news.dto.CommentDto;
import com.news.dto.CommentRequest;
import com.news.model.User;
import com.news.service.ICommentService;
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
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Commentaires", description = "Gestion des commentaires sur les posts")
public class CommentController {

    private final ICommentService commentService;

    @GetMapping
    . Params: page, pageSize")
    public ResponseEntity<Page<CommentDto>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, page, pageSize));
    }

    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(postId, request, user));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request, user));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok(Map.of("message", "Commentaire supprime avec succes"));
    }
}


