package com.clinica.salud.modules.dashboard.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecentTransactionResponse(
        String        invoiceId,
        LocalDateTime paidAt,
        String        patientFullName,
        String        description,
        BigDecimal    amount,
        String        status,
        String        paymentMethod
) {}
