package com.clinica.salud.modules.inventory.application.dto;

import com.clinica.salud.modules.inventory.domain.model.InventoryMovementType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryMovementResponse(
        UUID id,
        UUID itemId,
        InventoryMovementType type,
        int quantity,
        String lotNumber,
        LocalDate expiryDate,
        String reason,
        String reference,
        UUID createdBy,
        OffsetDateTime createdAt
) {
}

