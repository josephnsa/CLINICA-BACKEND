package com.clinica.salud.modules.dashboard.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicePerformanceResponse(
        UUID       doctorId,
        String     doctorName,
        String     serviceName,
        long       appointmentCount,
        BigDecimal revenueGenerated,
        String     priority
) {}
