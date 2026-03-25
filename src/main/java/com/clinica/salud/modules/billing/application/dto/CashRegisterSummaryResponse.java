package com.clinica.salud.modules.billing.application.dto;

import com.clinica.salud.modules.billing.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.Map;

public record CashRegisterSummaryResponse(
        long invoiceCount,
        Map<PaymentMethod, BigDecimal> totalByMethod,
        BigDecimal grandTotal
) {}
