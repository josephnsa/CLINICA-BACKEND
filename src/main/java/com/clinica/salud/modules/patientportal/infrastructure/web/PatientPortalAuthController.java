package com.clinica.salud.modules.patientportal.infrastructure.web;

import com.clinica.salud.modules.patientportal.application.dto.PatientLoginRequest;
import com.clinica.salud.modules.patientportal.application.dto.PatientLoginResponse;
import com.clinica.salud.modules.patientportal.application.dto.PatientRegisterRequest;
import com.clinica.salud.modules.patientportal.application.usecase.PatientLoginUseCase;
import com.clinica.salud.modules.patientportal.application.usecase.PatientRegisterUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal/auth")
@RequiredArgsConstructor
public class PatientPortalAuthController {

    private final PatientRegisterUseCase registerUseCase;
    private final PatientLoginUseCase loginUseCase;

    /**
     * Registro de paciente en el portal.
     * El paciente debe existir previamente en el sistema (creado por recepción).
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PatientLoginResponse>> register(
            @Valid @RequestBody PatientRegisterRequest request) {
        PatientLoginResponse response = registerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cuenta creada exitosamente", response));
    }

    /**
     * Inicio de sesión del paciente en el portal.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<PatientLoginResponse>> login(
            @Valid @RequestBody PatientLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(loginUseCase.execute(request)));
    }
}
