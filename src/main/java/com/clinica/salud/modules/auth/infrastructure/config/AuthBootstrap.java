package com.clinica.salud.modules.auth.infrastructure.config;

import com.clinica.salud.modules.auth.infrastructure.persistence.RoleEntity;
import com.clinica.salud.modules.auth.infrastructure.persistence.RoleJpaRepository;
import com.clinica.salud.modules.auth.infrastructure.persistence.UserEntity;
import com.clinica.salud.modules.auth.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthBootstrap implements CommandLineRunner {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        String adminEmail = "admin@clinica.pe";
        String rawPassword = "Admin2025!";

        Optional<UserEntity> existingOpt = userJpaRepository.findByEmail(adminEmail);

        if (existingOpt.isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setEmail(adminEmail);
            admin.setFullName("Administrador Sistema");
            admin.setPassword(passwordEncoder.encode(rawPassword));
            admin.setActive(true);

            Optional<RoleEntity> adminRoleOpt = roleJpaRepository.findAll().stream()
                    .filter(r -> "ADMIN".equals(r.getCode()))
                    .findFirst();
            adminRoleOpt.ifPresent(admin::setRole);

            userJpaRepository.save(admin);
        }
    }
}


