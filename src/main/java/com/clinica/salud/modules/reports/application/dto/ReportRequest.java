package com.clinica.salud.modules.reports.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ReportRequest {

    @NotNull(message = "La sede es obligatoria")
    private UUID sedeId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate dateFrom;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate dateTo;

    public void validate() {
        if (dateTo.isBefore(dateFrom)) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        if (java.time.temporal.ChronoUnit.DAYS.between(dateFrom, dateTo) > 366) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException(
                    "El rango del reporte no puede superar 366 días");
        }
    }
}
