package com.news.service;

import com.news.dto.PostDto;
import com.news.dto.PostRequest;
import com.news.model.Post;
import com.news.model.User;
import org.springframework.data.domain.Page;

public interface IPostService {

    Page<PostDto> getPublicFeed(int page, int pageSize, String category);

    PostDto getPostById(Long id);

    Page<PostDto> getMyPosts(User user, int page, int pageSize);

    PostDto createPost(PostRequest request, User author);

    PostDto updatePost(Long id, PostRequest request, User currentUser);

    void deletePost(Long id, User currentUser);

    void reportPost(Long id, User reporter);

    PostDto toDto(Post post);
}

