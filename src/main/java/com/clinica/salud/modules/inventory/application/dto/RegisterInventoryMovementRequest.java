package com.clinica.salud.modules.inventory.application.dto;

import com.clinica.salud.modules.inventory.domain.model.InventoryMovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record RegisterInventoryMovementRequest(
        @NotNull UUID medicationId,
        @NotNull UUID sedeId,
        @NotNull InventoryMovementType type,
        @Min(1) int quantity,
        String lotNumber,
        LocalDate expiryDate,
        String reason,
        String reference
) {
}

