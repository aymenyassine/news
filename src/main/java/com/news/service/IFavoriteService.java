package com.news.service;

import com.news.dto.FavoriteDto;
import com.news.model.User;

import java.util.List;

public interface IFavoriteService {

    List<FavoriteDto> getFavorites(User user);

    FavoriteDto addFavorite(FavoriteDto request, User user);

    void deleteFavorite(Long id, User user);

    boolean isFavorite(String articleUrl, User user);
}

