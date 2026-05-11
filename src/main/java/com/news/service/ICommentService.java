package com.news.service;

import com.news.dto.CommentDto;
import com.news.dto.CommentRequest;
import com.news.model.User;
import org.springframework.data.domain.Page;

public interface ICommentService {

    Page<CommentDto> getCommentsByPost(Long postId, int page, int pageSize);

    CommentDto addComment(Long postId, CommentRequest request, User author);

    CommentDto updateComment(Long commentId, CommentRequest request, User currentUser);

    void deleteComment(Long commentId, User currentUser);
}

