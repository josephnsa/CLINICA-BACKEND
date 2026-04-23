package com.clinica.salud.modules.auth.application.usecase;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.clinica.salud.modules.auth.application.dto.LoginResponse;
import com.clinica.salud.modules.auth.domain.model.User;
import com.clinica.salud.modules.auth.domain.port.UserRepository;
import com.clinica.salud.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleLoginUseCase {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final UserRepository userRepository;

    public LoginResponse execute(String idToken) {
        GoogleIdToken googleIdToken = verify(idToken);

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();

        User user = userRepository.findByGoogleId(googleId)
                .or(() -> userRepository.findByEmail(email.trim().toLowerCase()))
                .orElseThrow(() -> new UnauthorizedException(
                        "No tienes acceso al sistema. Contacta al administrador."));

        if (!user.isActive()) {
            throw new UnauthorizedException("Usuario inactivo");
        }

        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            user.setAuthProvider("GOOGLE");
            userRepository.save(user);
        }

        List<String> permissions = Optional.ofNullable(user.getPermissions()).orElse(Collections.emptyList());
        String role = user.getRoleCode() != null ? user.getRoleCode() : "";

        return new LoginResponse(idToken, null, user.getFullName() != null ? user.getFullName() : "", role, permissions);
    }

    private GoogleIdToken verify(String idToken) {
        try {
            log.info("Verificando Google ID token, audience configurado: {}",
                    googleIdTokenVerifier.getAudience());
            GoogleIdToken token = googleIdTokenVerifier.verify(idToken);
            if (token == null) {
                log.warn("Google token verification returned null - audience o firma no coinciden");
                throw new UnauthorizedException("Token de Google inválido o expirado");
            }
            return token;
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error al verificar token de Google: {}", e.getMessage());
            throw new UnauthorizedException("Error al verificar el token de Google");
        }
    }
}
