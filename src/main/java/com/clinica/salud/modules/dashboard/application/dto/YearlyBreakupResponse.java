package com.clinica.salud.modules.dashboard.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record YearlyBreakupResponse(
        List<YearSummary> years
) {
    public record YearSummary(
            int        year,
            BigDecimal totalRevenue,
            Double     growthPercent
    ) {}
}
