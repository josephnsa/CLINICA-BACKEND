package com.clinica.salud.modules.prescription.infrastructure.web;

import com.clinica.salud.modules.prescription.application.dto.CreatePrescriptionRequest;
import com.clinica.salud.modules.prescription.application.dto.PrescriptionResponse;
import com.clinica.salud.modules.prescription.application.usecase.CancelPrescriptionUseCase;
import com.clinica.salud.modules.prescription.application.usecase.CreatePrescriptionUseCase;
import com.clinica.salud.modules.prescription.application.usecase.DispensePrescriptionUseCase;
import com.clinica.salud.modules.prescription.application.usecase.GetPrescriptionsByPatientUseCase;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;
    private final GetPrescriptionsByPatientUseCase getPrescriptionsByPatientUseCase;
    private final DispensePrescriptionUseCase dispensePrescriptionUseCase;
    private final CancelPrescriptionUseCase cancelPrescriptionUseCase;
    private final JdbcTemplate jdbcTemplate;

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

    @PatchMapping("/api/prescriptions/{id}/cancel")
    @PreAuthorize("hasAuthority('PRESCRIPCIONES_CREATE')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> cancel(@PathVariable UUID id) {
        PrescriptionResponse response = cancelPrescriptionUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/patients/{patientId}/kardex")
    @PreAuthorize("hasAuthority('PRESCRIPCIONES_READ')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getKardex(
            @PathVariable UUID patientId) {
        List<Map<String, Object>> kardex = jdbcTemplate.queryForList("""
            SELECT k.id,
                   k.patient_id      AS "patientId",
                   k.prescription_id AS "prescriptionId",
                   k.medication_id   AS "medicationId",
                   m.generic_name    AS "medicationName",
                   m.commercial_name AS "commercialName",
                   k.action,
                   k.quantity,
                   k.notes,
                   k.recorded_by     AS "recordedBy",
                   k.recorded_at     AS "recordedAt"
            FROM medication_kardex k
            LEFT JOIN medications m ON k.medication_id = m.id
            WHERE k.patient_id = ?
            ORDER BY k.recorded_at DESC
            """, patientId);
        return ResponseEntity.ok(ApiResponse.ok(kardex));
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

