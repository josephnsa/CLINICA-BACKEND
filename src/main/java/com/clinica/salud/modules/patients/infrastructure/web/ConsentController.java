package com.clinica.salud.modules.patients.infrastructure.web;

import com.clinica.salud.modules.patients.application.usecase.CreateConsentUseCase;
import com.clinica.salud.modules.patients.domain.model.PatientConsent;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients/{patientId}/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final CreateConsentUseCase createConsentUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('patients:write')")
    public ResponseEntity<ApiResponse<PatientConsent>> create(
            @PathVariable UUID patientId,
            @Valid @RequestBody CreateConsentRequest request) {
        UUID createdBy = getCurrentUserId();
        PatientConsent consent = createConsentUseCase.execute(
                patientId, request.type(), request.fileUrl(), createdBy);
        return ResponseEntity.ok(ApiResponse.ok(consent));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('patients:read')")
    public ResponseEntity<ApiResponse<List<PatientConsent>>> list(@PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.ok(createConsentUseCase.getByPatient(patientId)));
    }

    @DeleteMapping("/{consentId}")
    @PreAuthorize("hasAuthority('patients:write')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID patientId,
            @PathVariable UUID consentId) {
        createConsentUseCase.delete(consentId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String)) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Identidad de usuario inválida");
        }
    }

    public record CreateConsentRequest(@NotBlank String type, String fileUrl) {}
}
