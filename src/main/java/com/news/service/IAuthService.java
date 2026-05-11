package com.news.service;

import com.news.dto.AuthResponse;
import com.news.dto.LoginRequest;
import com.news.dto.RegisterRequest;
import com.news.model.User;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    void logout(User user);
}

