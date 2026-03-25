package com.clinica.salud.modules.agenda.infrastructure.web;

import com.clinica.salud.modules.agenda.application.dto.RegisterTriageRequest;
import com.clinica.salud.modules.agenda.application.dto.TriageResponse;
import com.clinica.salud.modules.agenda.application.usecase.RegisterTriageUseCase;
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
@RequestMapping("/api/triage")
@RequiredArgsConstructor
public class TriageController {

    private final RegisterTriageUseCase registerTriageUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('agenda:triage')")
    public ResponseEntity<ApiResponse<TriageResponse>> register(
            @Valid @RequestBody RegisterTriageRequest request) {
        UUID recordedBy = getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.ok(registerTriageUseCase.execute(request, recordedBy)));
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAuthority('agenda:read')")
    public ResponseEntity<ApiResponse<TriageResponse>> getByAppointment(
            @PathVariable UUID appointmentId) {
        return ResponseEntity.ok(ApiResponse.ok(registerTriageUseCase.getByAppointment(appointmentId)));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('agenda:read')")
    public ResponseEntity<ApiResponse<List<TriageResponse>>> getByPatient(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.ok(registerTriageUseCase.getByPatient(patientId)));
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
}
