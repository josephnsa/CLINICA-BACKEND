package com.clinica.salud.modules.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    private UUID id;
    private String serie;
    private Integer number;
    private UUID patientId;
    private UUID sedeId;
    private UUID appointmentId;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private InvoiceStatus status;
    private String notes;
    private UUID createdBy;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
}
