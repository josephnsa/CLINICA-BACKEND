package com.clinica.salud.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Responde OPTIONS /api/** con 204 y cabeceras CORS antes de CorsFilter / Security,
 * para entornos detrás de API Gateway donde el preflight seguía recibiendo 403.
 */
@Component
public class ApiPreflightOptionsFilter extends OncePerRequestFilter {

    private static final Pattern WEB_APP = Pattern.compile("^https://[\\w.-]+\\.web\\.app$", Pattern.CASE_INSENSITIVE);
    private static final Pattern FIREBASE_APP =
            Pattern.compile("^https://[\\w.-]+\\.firebaseapp\\.com$", Pattern.CASE_INSENSITIVE);

    private static final String DEFAULT_ALLOW_HEADERS =
            "Authorization, Content-Type, Accept, Origin, X-Requested-With, X-Forwarded-Authorization";

    @Value("${app.cors.allowed-origins:}")
    private String allowedOriginsProp;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if (!isAllowedOrigin(origin)) {
            filterChain.doFilter(request, response);
            return;
        }
        String reqHeaders = request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(
                HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,PATCH,DELETE,OPTIONS");
        if (reqHeaders != null && !reqHeaders.isBlank()) {
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, reqHeaders);
        } else {
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, DEFAULT_ALLOW_HEADERS);
        }
        response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(HttpHeaders.VARY, HttpHeaders.ORIGIN);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private boolean isAllowedOrigin(String origin) {
        if (origin == null || origin.isBlank()) {
            return false;
        }
        if (origin.startsWith("http://localhost:") || "http://localhost:4200".equals(origin)) {
            return true;
        }
        if (WEB_APP.matcher(origin).matches() || FIREBASE_APP.matcher(origin).matches()) {
            return true;
        }
        if (allowedOriginsProp == null || allowedOriginsProp.isBlank()) {
            return false;
        }
        List<String> extras = Arrays.stream(allowedOriginsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        return extras.contains(origin);
    }
}
