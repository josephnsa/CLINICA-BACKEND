package com.clinica.salud.modules.inventory.application.dto;

import java.util.UUID;

public record InventoryItemResponse(
        UUID id,
        UUID medicationId,
        UUID sedeId,
        int stock,
        int minStock
) {
}

