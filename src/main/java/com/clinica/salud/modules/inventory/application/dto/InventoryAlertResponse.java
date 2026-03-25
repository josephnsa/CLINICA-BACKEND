package com.clinica.salud.modules.inventory.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record InventoryAlertResponse(
        UUID itemId,
        UUID medicationId,
        UUID sedeId,
        int stock,
        int minStock,
        LocalDate nearestExpiryDate
) {
}

