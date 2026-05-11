package com.news.repository;

import com.news.model.Comment;
import com.news.model.Post;
import com.news.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPost(Post post, Pageable pageable);

    long countByPost(Post post);

    Page<Comment> findByAuthor(User author, Pageable pageable);
}

