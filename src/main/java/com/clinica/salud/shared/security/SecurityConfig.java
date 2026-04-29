package com.clinica.salud.shared.security;

import com.clinica.salud.modules.auth.infrastructure.security.GoogleJwtAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final GoogleJwtAuthFilter googleJwtAuthFilter;
    private final ApiPreflightOptionsFilter apiPreflightOptionsFilter;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            GoogleJwtAuthFilter googleJwtAuthFilter,
            ApiPreflightOptionsFilter apiPreflightOptionsFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.googleJwtAuthFilter = googleJwtAuthFilter;
        this.apiPreflightOptionsFilter = apiPreflightOptionsFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/refresh", "/api/auth/google/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/portal/auth/register", "/api/portal/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(apiPreflightOptionsFilter, CorsFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(googleJwtAuthFilter, JwtAuthFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = parseOrigins(allowedOrigins);
        if (origins.isEmpty()) {
            log.warn(
                    "app.cors.allowed-origins está vacío (revisa CORS_ALLOWED_ORIGINS en Cloud Run / gcp.env). "
                            + "Se usa patrón amplio sin credenciales CORS para que el servicio pueda arrancar.");
            config.setAllowedOriginPatterns(List.of("*"));
            config.setAllowCredentials(false);
        } else {
            // Patrones: permiten credenciales con subdominios Firebase y localhost sin listar cada preview URL.
            // Orígenes explícitos de app.cors (p. ej. dominio custom) se añaden como patrones literales.
            Set<String> patterns = new LinkedHashSet<>();
            patterns.add("http://localhost:*");
            patterns.add("https://*.web.app");
            patterns.add("https://*.firebaseapp.com");
            origins.forEach(patterns::add);
            config.setAllowedOriginPatterns(new ArrayList<>(patterns));
            config.setAllowCredentials(true);
        }
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private static List<String> parseOrigins(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
