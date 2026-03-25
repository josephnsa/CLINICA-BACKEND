package com.clinica.salud.modules.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItem {

    private UUID id;
    private UUID invoiceId;
    private UUID serviceId;
    private String description;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
}
