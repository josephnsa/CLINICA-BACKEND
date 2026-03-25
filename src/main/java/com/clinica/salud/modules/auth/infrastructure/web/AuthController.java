package com.clinica.salud.modules.auth.infrastructure.web;

import com.clinica.salud.modules.auth.application.dto.LoginRequest;
import com.clinica.salud.modules.auth.application.dto.LoginResponse;
import com.clinica.salud.modules.auth.application.dto.MeResponse;
import com.clinica.salud.modules.auth.application.dto.MenuItemDto;
import com.clinica.salud.modules.auth.application.dto.RefreshResponse;
import com.clinica.salud.modules.auth.application.usecase.LoginUseCase;
import com.clinica.salud.modules.auth.domain.model.MenuItem;
import com.clinica.salud.modules.auth.domain.model.User;
import com.clinica.salud.modules.auth.domain.port.MenuRepository;
import com.clinica.salud.modules.auth.domain.port.UserRepository;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import com.clinica.salud.shared.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_HEADER = "Refresh-Token";

    private final LoginUseCase loginUseCase;
    private final JwtService jwtService;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(
            @RequestHeader(value = REFRESH_TOKEN_HEADER, required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token requerido");
        }
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new UnauthorizedException("Refresh token inválido o expirado");
        }
        String userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
        if (!user.isActive()) {
            throw new UnauthorizedException("Usuario inactivo");
        }
        String role = Optional.ofNullable(user.getRoleCode()).orElse("");
        List<String> permissions = Optional.ofNullable(user.getPermissions()).orElse(Collections.emptyList());
        String sedeId = user.getSedeIds() != null && !user.getSedeIds().isEmpty()
                ? user.getSedeIds().get(0).toString()
                : "";
        String accessToken = jwtService.generateToken(userId, role, permissions, sedeId);
        return ResponseEntity.ok(ApiResponse.ok(new RefreshResponse(accessToken)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("No autenticado");
        }
        String userId = auth.getPrincipal().toString();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
        MeResponse data = new MeResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName() != null ? user.getFullName() : "",
                user.getRoleCode() != null ? user.getRoleCode() : "",
                user.getPermissions() != null ? user.getPermissions() : List.of()
        );
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/menu")
    public ResponseEntity<ApiResponse<List<MenuItemDto>>> menu() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("No autenticado");
        }
        String userId = auth.getPrincipal().toString();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        String roleCode = Optional.ofNullable(user.getRoleCode()).orElse("");
        if (roleCode.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(List.of()));
        }

        List<MenuItem> items = menuRepository.findMenuByRoleCode(roleCode);
        List<MenuItemDto> dto = items.stream().map(this::toMenuItemDto).toList();
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    private MenuItemDto toMenuItemDto(MenuItem item) {
        List<MenuItemDto> children = item.getChildren() != null
                ? item.getChildren().stream().map(this::toMenuItemDto).toList()
                : List.of();
        return new MenuItemDto(
                item.getId(),
                item.getLabel(),
                item.getIcon(),
                item.getRoute(),
                children
        );
    }
}
