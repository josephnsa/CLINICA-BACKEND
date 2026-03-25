package com.clinica.salud.modules.billing.application.dto;

import com.clinica.salud.modules.billing.domain.model.Invoice;
import com.clinica.salud.modules.billing.domain.model.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record InvoiceResponse(
        UUID id,
        String serie,
        Integer number,
        UUID patientId,
        UUID sedeId,
        UUID appointmentId,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        InvoiceStatus status,
        String notes,
        UUID createdBy,
        LocalDateTime createdAt,
        List<InvoiceItemResponse> items,
        List<PaymentResponse> payments
) {
    public static InvoiceResponse from(Invoice inv) {
        return new InvoiceResponse(
                inv.getId(),
                inv.getSerie(),
                inv.getNumber(),
                inv.getPatientId(),
                inv.getSedeId(),
                inv.getAppointmentId(),
                inv.getSubtotal(),
                inv.getTax(),
                inv.getTotal(),
                inv.getStatus(),
                inv.getNotes(),
                inv.getCreatedBy(),
                inv.getCreatedAt(),
                inv.getItems() != null ? inv.getItems().stream().map(InvoiceItemResponse::from).collect(Collectors.toList()) : List.of(),
                inv.getPayments() != null ? inv.getPayments().stream().map(PaymentResponse::from).collect(Collectors.toList()) : List.of()
        );
    }
}
