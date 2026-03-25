package com.clinica.salud.modules.clinical.infrastructure.web;

import com.clinica.salud.modules.clinical.application.dto.ClinicalNoteResponse;
import com.clinica.salud.modules.clinical.application.dto.CreateClinicalNoteRequest;
import com.clinica.salud.modules.clinical.application.usecase.CreateClinicalNoteUseCase;
import com.clinica.salud.modules.clinical.application.usecase.GetPatientHistoryUseCase;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClinicalNoteController {

    private final CreateClinicalNoteUseCase createClinicalNoteUseCase;
    private final GetPatientHistoryUseCase getPatientHistoryUseCase;

    @PostMapping("/api/clinical-notes")
    @PreAuthorize("hasAuthority('HISTORIA_CLINICA_WRITE')")
    public ResponseEntity<ApiResponse<ClinicalNoteResponse>> create(@Valid @RequestBody CreateClinicalNoteRequest request) {
        UUID currentUserId = getCurrentUserId();
        ClinicalNoteResponse response = createClinicalNoteUseCase.execute(request, currentUserId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/patients/{patientId}/history")
    @PreAuthorize("hasAuthority('HISTORIA_CLINICA_READ')")
    public ResponseEntity<ApiResponse<Page<ClinicalNoteResponse>>> getPatientHistory(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ClinicalNoteResponse> page = getPatientHistoryUseCase.execute(patientId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
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
