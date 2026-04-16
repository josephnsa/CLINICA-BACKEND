package com.clinica.salud.modules.patientportal.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PatientLoginResponse {

    private String accessToken;
    private String refreshToken;
    private UUID patientId;
    private String fullName;
    private String email;
    private List<String> permissions;
}
