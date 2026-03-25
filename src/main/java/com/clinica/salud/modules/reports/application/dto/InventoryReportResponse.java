package com.clinica.salud.modules.reports.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InventoryReportResponse {

    private UUID sedeId;
    private LocalDate reportDate;

    private long totalItems;
    private long lowStockItems;
    private long outOfStockItems;
    private long expiringIn30Days;
    private long expiredItems;

    private List<InventoryItemSummary> criticalItems;

    @Data
    @Builder
    public static class InventoryItemSummary {
        private UUID medicationId;
        private String medicationName;
        private int currentStock;
        private int minStock;
        private LocalDate nearestExpiry;
        private String alertType;   // LOW_STOCK, EXPIRING_SOON, EXPIRED
    }
}
