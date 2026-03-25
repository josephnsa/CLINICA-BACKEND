package com.clinica.salud.modules.hrm.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateEmployeeRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    private String docType;
    private String docNumber;
    private String licenseNumber;

    @NotBlank(message = "El cargo es obligatorio")
    private String position;

    private UUID sedeId;
    private UUID specialtyId;
    private UUID userId;
    private LocalDate hireDate;
    private String phone;
    private String email;
}
