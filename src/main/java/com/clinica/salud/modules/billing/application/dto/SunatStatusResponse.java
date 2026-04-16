package com.clinica.salud.modules.billing.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SunatStatusResponse(
        UUID id,
        UUID invoiceId,
        String documentType,
        String status,
        String ticket,
        String errorMessage,
        LocalDateTime sentAt,
        LocalDateTime createdAt
) {}
