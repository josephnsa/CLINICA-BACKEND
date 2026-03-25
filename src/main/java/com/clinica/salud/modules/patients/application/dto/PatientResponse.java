package com.clinica.salud.modules.patients.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String docType,
        String docNumber,
        String firstName,
        String lastName,
        String fullName,
        int age,
        LocalDate birthDate,
        String gender,
        String email,
        String phone,
        String address,
        String bloodType,
        String emergencyName,
        String emergencyPhone,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
