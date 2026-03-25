package com.clinica.salud.modules.customerservice.infrastructure.web;

import com.clinica.salud.modules.customerservice.application.dto.ComplaintResponse;
import com.clinica.salud.modules.customerservice.application.dto.CreateComplaintRequest;
import com.clinica.salud.modules.customerservice.application.dto.CreateSurveyRequest;
import com.clinica.salud.modules.customerservice.application.dto.SurveyResponse;
import com.clinica.salud.modules.customerservice.application.usecase.CreateComplaintUseCase;
import com.clinica.salud.modules.customerservice.application.usecase.CreateSurveyUseCase;
import com.clinica.salud.modules.customerservice.application.usecase.ResolveComplaintUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ComplaintController {

    private final CreateComplaintUseCase createComplaintUseCase;
    private final ResolveComplaintUseCase resolveComplaintUseCase;
    private final CreateSurveyUseCase createSurveyUseCase;

    @PostMapping("/api/customer-service/complaints")
    @PreAuthorize("hasAuthority('ATENCION_CLIENTE_WRITE')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> create(
            @Valid @RequestBody CreateComplaintRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(createComplaintUseCase.execute(request)));
    }

    @PatchMapping("/api/customer-service/complaints/{id}/resolve")
    @PreAuthorize("hasAuthority('ATENCION_CLIENTE_WRITE')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> resolve(
            @PathVariable UUID id,
            @RequestParam String resolution) {
        return ResponseEntity.ok(ApiResponse.ok(resolveComplaintUseCase.execute(id, resolution)));
    }

    @PostMapping("/api/customer-service/surveys")
    @PreAuthorize("hasAuthority('ATENCION_CLIENTE_WRITE')")
    public ResponseEntity<ApiResponse<SurveyResponse>> createSurvey(
            @Valid @RequestBody CreateSurveyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(createSurveyUseCase.execute(request)));
    }
}
