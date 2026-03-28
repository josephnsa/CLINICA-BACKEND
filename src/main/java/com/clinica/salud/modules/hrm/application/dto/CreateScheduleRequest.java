package com.clinica.salud.modules.hrm.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateScheduleRequest {

    @NotNull(message = "El empleado es obligatorio")
    private UUID employeeId;

    private UUID sedeId;

    @Min(value = 0, message = "Día mínimo es 0 (Lunes)")
    @Max(value = 6, message = "Día máximo es 6 (Domingo)")
    private int dayOfWeek;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;
}
