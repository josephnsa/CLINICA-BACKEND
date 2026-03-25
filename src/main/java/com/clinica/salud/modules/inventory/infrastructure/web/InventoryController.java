package com.clinica.salud.modules.inventory.infrastructure.web;

import com.clinica.salud.modules.inventory.application.dto.InventoryAlertResponse;
import com.clinica.salud.modules.inventory.application.dto.InventoryItemResponse;
import com.clinica.salud.modules.inventory.application.dto.RegisterInventoryMovementRequest;
import com.clinica.salud.modules.inventory.application.usecase.GetLowStockAlertsUseCase;
import com.clinica.salud.modules.inventory.application.usecase.RegisterInventoryMovementUseCase;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final RegisterInventoryMovementUseCase registerInventoryMovementUseCase;
    private final GetLowStockAlertsUseCase getLowStockAlertsUseCase;

    @PostMapping("/api/inventory/movements")
    @PreAuthorize("hasAnyAuthority('INVENTARIO_CREATE', 'FARMACIA_CREATE')")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> registerMovement(
            @Valid @RequestBody RegisterInventoryMovementRequest request) {
        UUID userId = getCurrentUserId();
        InventoryItemResponse response = registerInventoryMovementUseCase.execute(request, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/inventory/alerts/low-stock")
    @PreAuthorize("hasAnyAuthority('INVENTARIO_READ', 'FARMACIA_READ')")
    public ResponseEntity<ApiResponse<List<InventoryAlertResponse>>> getLowStockAlerts(
            @RequestParam UUID sedeId) {
        List<InventoryAlertResponse> alerts = getLowStockAlertsUseCase.execute(sedeId);
        return ResponseEntity.ok(ApiResponse.ok(alerts));
    }

    private static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof String)) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Identidad de usuario inválida");
        }
    }
}

