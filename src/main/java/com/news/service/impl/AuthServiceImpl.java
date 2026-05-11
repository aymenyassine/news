package com.news.service.impl;

import com.news.dto.AuthResponse;
import com.news.dto.LoginRequest;
import com.news.dto.RegisterRequest;
import com.news.enums.Role;
import com.news.exception.BadRequestException;
import com.news.model.RefreshToken;
import com.news.model.User;
import com.news.repository.RefreshTokenRepository;
import com.news.repository.UserRepository;
import com.news.security.JwtService;
import com.news.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Un compte existe deja avec cet email");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.USER)
                .banned(false)
                .build();

        userRepository.save(user);
        log.info("Nouvel utilisateur inscrit : {}", user.getEmail());

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Verifie les credentials (lance BadCredentialsException si invalide)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouve"));

        // Verification bannissement
        if (user.isBanned()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ACCOUNT_BANNED");
        }

        log.info("Connexion reussie : {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BadRequestException("Refresh token invalide"));

        if (refreshToken.isRevoked()) {
            throw new BadRequestException("Refresh token revoque");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expire");
        }

        User user = refreshToken.getUser();

        // Verification bannissement avant emission d'un nouveau token
        if (user.isBanned()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ACCOUNT_BANNED");
        }

        // Revoquer l'ancien refresh token et en generer un nouveau
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void logout(User user) {
        refreshTokenRepository.revokeAllByUser(user);
        log.info("Logout : {}", user.getEmail());
    }

    // ===== Methodes privees =====

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshTokenValue)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .build();
    }
}

