package com.clinica.salud.modules.reports.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class FinancialReportResponse {

    private UUID sedeId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    // Ingresos
    private BigDecimal totalBilled;
    private BigDecimal totalCollected;
    private BigDecimal totalPending;
    private BigDecimal totalRefunded;

    // Desglose por método de pago
    private BigDecimal cashAmount;
    private BigDecimal cardAmount;
    private BigDecimal transferAmount;
    private BigDecimal insuranceAmount;

    // Promedios
    private BigDecimal avgInvoiceAmount;
    private long totalInvoices;
    private long paidInvoices;
    private long pendingInvoices;
}
