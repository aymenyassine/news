package com.news.repository;

import com.news.model.Favorite;
import com.news.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserOrderBySavedAtDesc(User user);

    Optional<Favorite> findByUserAndArticleUrl(User user, String articleUrl);

    boolean existsByUserAndArticleUrl(User user, String articleUrl);

    void deleteByUserAndArticleUrl(User user, String articleUrl);
}
