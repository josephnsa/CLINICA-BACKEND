package com.clinica.salud.modules.hrm.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EmployeeResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String docType;
    private String docNumber;
    private String licenseNumber;
    private String position;
    private UUID sedeId;
    private UUID specialtyId;
    private LocalDate hireDate;
    private int seniority;
    private boolean isActive;
    private boolean isLicensed;
    private String phone;
    private String email;
}
