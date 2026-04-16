package com.clinica.salud.modules.hrm.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterCheckInRequest {

    @NotNull(message = "El ID del empleado es obligatorio")
    private UUID employeeId;

    @NotNull(message = "La sede es obligatoria")
    private UUID sedeId;

    private String notes;
}
