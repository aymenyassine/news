package com.news.service;

import com.news.dto.AuthResponse;
import com.news.dto.LoginRequest;
import com.news.dto.RegisterRequest;
import com.news.model.User;

/**
 * Contrat du service d'authentification.
 */
public interface IAuthService {

    /**
     * Inscription d'un nouvel utilisateur avec role USER par defaut.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Connexion — retourne JWT access + refresh token.
     * Retourne 403 ACCOUNT_BANNED si le compte est banni.
     */
    AuthResponse login(LoginRequest request);

    /**
     * Renouvellement du token d'acces via refresh token.
     * Verifie que le compte n'est pas banni avant d'emettre un nouveau token.
     */
    AuthResponse refresh(String refreshToken);

    /**
     * Logout — invalide tous les refresh tokens de l'utilisateur.
     */
    void logout(User user);
}
