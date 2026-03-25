package com.clinica.salud.modules.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    private UUID id;
    private UUID invoiceId;
    private PaymentMethod method;
    private BigDecimal amount;
    private String reference;
    private LocalDateTime paidAt;
    private UUID createdBy;
}
