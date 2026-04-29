package com.clinica.salud.modules.auth.infrastructure.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.clinica.salud.modules.auth.domain.model.User;
import com.clinica.salud.modules.auth.domain.port.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoogleJwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String FORWARDED_AUTHORIZATION_HEADER = "X-Forwarded-Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // API Gateway (ESPv2) replaces Authorization with SA token and moves client token here
        String authHeader = request.getHeader(FORWARDED_AUTHORIZATION_HEADER);
        if (authHeader == null) {
            authHeader = request.getHeader(AUTHORIZATION_HEADER);
        }
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        GoogleIdToken googleIdToken = verifyQuietly(token);
        if (googleIdToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = googleIdToken.getPayload().getEmail();
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());

        if (userOpt.isPresent() && userOpt.get().isActive()) {
            User user = userOpt.get();

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (user.getPermissions() != null) {
                user.getPermissions().stream()
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }
            String role = user.getRoleCode();
            if (role != null && !role.isBlank()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getId().toString(),
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private GoogleIdToken verifyQuietly(String token) {
        try {
            return googleIdTokenVerifier.verify(token);
        } catch (Exception e) {
            return null;
        }
    }
}
