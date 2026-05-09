package com.news.config;

import com.news.enums.Role;
import com.news.model.User;
import com.news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initialisation du compte ADMIN par defaut au demarrage.
 * Le role ADMIN ne peut etre attribue que via ce DataInitializer
 * ou manuellement en base — jamais via l'API publique.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default-email}")
    private String adminEmail;

    @Value("${admin.default-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .name("Administrateur")
                    .role(Role.ADMIN)
                    .banned(false)
                    .build();
            userRepository.save(admin);
            log.info("Compte ADMIN cree : {}", adminEmail);
        } else {
            log.info("Compte ADMIN existant : {}", adminEmail);
        }
    }
}
