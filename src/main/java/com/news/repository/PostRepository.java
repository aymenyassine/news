package com.news.repository;

import com.news.enums.PostStatus;
import com.news.model.Post;
import com.news.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    Page<Post> findByStatusAndCategory(PostStatus status, String category, Pageable pageable);

    Page<Post> findByAuthor(User author, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
           "(:status IS NULL OR p.status = :status) " +
           "AND (:reported IS NULL OR (:reported = true AND p.reportCount > 0))")
    Page<Post> findAllWithFilters(@Param("status") PostStatus status,
                                  @Param("reported") Boolean reported,
                                  Pageable pageable);

    long countByStatus(PostStatus status);

    long countByReportCountGreaterThan(int threshold);

    long countByAuthor(User author);
}

