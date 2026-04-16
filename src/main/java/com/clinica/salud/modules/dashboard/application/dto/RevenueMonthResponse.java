package com.clinica.salud.modules.dashboard.application.dto;

import java.math.BigDecimal;

public record RevenueMonthResponse(
        int        month,
        String     monthLabel,
        BigDecimal totalInvoiced,
        long       appointmentCount
) {}
