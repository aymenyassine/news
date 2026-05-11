package com.news.service;

import com.news.dto.ArticleHistoryDto;
import com.news.dto.UserProfileDto;
import com.news.model.User;

import java.util.List;

public interface IUserService {

    UserProfileDto getProfile(User user);

    UserProfileDto updateProfile(User user, UserProfileDto dto);

    List<ArticleHistoryDto> getHistory(User user);

    ArticleHistoryDto addToHistory(User user, ArticleHistoryDto dto);
}

