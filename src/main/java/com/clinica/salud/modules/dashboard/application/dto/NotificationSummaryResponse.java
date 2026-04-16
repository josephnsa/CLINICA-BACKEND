package com.clinica.salud.modules.dashboard.application.dto;

import java.util.List;

public record NotificationSummaryResponse(
        int                  totalUnread,
        int                  appointmentsToday,
        int                  pendingExamResults,
        int                  pendingInvoices,
        int                  lowStockItems,
        List<NotificationItem> recent
) {
    public record NotificationItem(
            String type,       // "APPOINTMENT" | "EXAM_RESULT" | "INVOICE" | "STOCK"
            String title,
            String body,
            String severity,   // "info" | "warning" | "danger" | "success"
            String createdAt,
            String linkId      // UUID del recurso relacionado (para navegación)
    ) {}
}
