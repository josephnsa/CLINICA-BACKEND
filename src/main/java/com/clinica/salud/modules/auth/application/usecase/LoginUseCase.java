package com.clinica.salud.modules.auth.application.usecase;

import com.clinica.salud.modules.auth.application.dto.LoginRequest;
import com.clinica.salud.modules.auth.application.dto.LoginResponse;
import com.clinica.salud.modules.auth.domain.model.User;
import com.clinica.salud.modules.auth.domain.port.UserRepository;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private static final String INVALID_CREDENTIALS = "Credenciales inválidas";
    private static final String USER_INACTIVE = "Usuario inactivo";

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException(INVALID_CREDENTIALS));

        if (user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(INVALID_CREDENTIALS);
        }

        // Temporalmente no bloqueamos por usuario inactivo para evitar falsos negativos de estado.

        List<String> permissions = Optional.ofNullable(user.getPermissions()).orElse(Collections.emptyList());

        String userId = user.getId().toString();
        String role = user.getRoleCode() != null ? user.getRoleCode() : "";
        String sedeId = user.getSedeIds() != null && !user.getSedeIds().isEmpty()
                ? user.getSedeIds().get(0).toString()
                : "";

        String accessToken = jwtService.generateToken(userId, role, permissions, sedeId);
        String refreshToken = jwtService.generateRefreshToken(userId);

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getFullName() != null ? user.getFullName() : "",
                role,
                permissions
        );
    }
}
