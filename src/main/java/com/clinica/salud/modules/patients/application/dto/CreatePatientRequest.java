package com.clinica.salud.modules.patients.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePatientRequest(
        @NotBlank(message = "docType es obligatorio") @Size(max = 10) String docType,
        @NotBlank(message = "docNumber es obligatorio") @Size(max = 20) String docNumber,
        @NotBlank(message = "firstName es obligatorio") @Size(max = 100) String firstName,
        @NotBlank(message = "lastName es obligatorio") @Size(max = 100) String lastName,
        LocalDate birthDate,
        @Size(max = 10) String gender,
        @Email(message = "email debe ser válido") @Size(max = 200) String email,
        @Size(max = 20) String phone,
        @Size(max = 300) String address,
        @Size(max = 5) String bloodType,
        @Size(max = 200) String emergencyName,
        @Size(max = 20) String emergencyPhone
) {}
