package com.clinica.salud.modules.hrm.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterCheckOutRequest {

    @NotNull(message = "El ID del empleado es obligatorio")
    private UUID employeeId;
}
