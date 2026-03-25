package com.clinica.salud.modules.prescription.infrastructure.web;

import com.clinica.salud.modules.prescription.application.dto.CreatePrescriptionRequest;
import com.clinica.salud.modules.prescription.application.dto.PrescriptionResponse;
import com.clinica.salud.modules.prescription.application.usecase.CreatePrescriptionUseCase;
import com.clinica.salud.modules.prescription.application.usecase.DispensePrescriptionUseCase;
import com.clinica.salud.modules.prescription.application.usecase.GetPrescriptionsByPatientUseCase;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;
    private final GetPrescriptionsByPatientUseCase getPrescriptionsByPatientUseCase;
    private final DispensePrescriptionUseCase dispensePrescriptionUseCase;

    @PostMapping("/api/prescriptions")
    @PreAuthorize("hasAuthority('PRESCRIPCIONES_CREATE')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> create(
            @Valid @RequestBody CreatePrescriptionRequest request) {
        UUID userId = getCurrentUserId();
        PrescriptionResponse response = createPrescriptionUseCase.execute(request, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/prescriptions")
    @PreAuthorize("hasAuthority('PRESCRIPCIONES_READ')")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getByPatient(
            @RequestParam UUID patientId) {
        List<PrescriptionResponse> response = getPrescriptionsByPatientUseCase.execute(patientId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/api/prescriptions/{id}/dispense")
    @PreAuthorize("hasAuthority('PRESCRIPCIONES_DISPENSE')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> dispense(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        PrescriptionResponse response = dispensePrescriptionUseCase.execute(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof String)) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Identidad de usuario inválida");
        }
    }
}

