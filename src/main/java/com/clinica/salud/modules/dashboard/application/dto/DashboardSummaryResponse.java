package com.clinica.salud.modules.dashboard.application.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        BigDecimal currentMonthRevenue,
        double     monthOverMonthChange,
        BigDecimal currentYearRevenue,
        double     yearOverYearChange,
        long       currentMonthAppointments,
        long       newPatientsThisMonth
) {}
