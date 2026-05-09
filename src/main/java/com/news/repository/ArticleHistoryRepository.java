package com.news.repository;

import com.news.model.ArticleHistory;
import com.news.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleHistoryRepository extends JpaRepository<ArticleHistory, Long> {

    List<ArticleHistory> findByUserOrderByViewedAtDesc(User user, Pageable pageable);

    long countByUser(User user);

    @Modifying
    @Query("DELETE FROM ArticleHistory h WHERE h.id IN " +
           "(SELECT h2.id FROM ArticleHistory h2 WHERE h2.user = :user " +
           "ORDER BY h2.viewedAt ASC LIMIT :excess)")
    void deleteOldestByUser(@Param("user") User user, @Param("excess") int excess);
}
