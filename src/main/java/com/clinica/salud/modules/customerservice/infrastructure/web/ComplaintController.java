package com.clinica.salud.modules.customerservice.infrastructure.web;

import com.clinica.salud.modules.customerservice.application.dto.ComplaintResponse;
import com.clinica.salud.modules.customerservice.application.dto.CreateComplaintRequest;
import com.clinica.salud.modules.customerservice.application.dto.CreateSurveyRequest;
import com.clinica.salud.modules.customerservice.application.dto.SurveyResponse;
import com.clinica.salud.modules.customerservice.application.usecase.CreateComplaintUseCase;
import com.clinica.salud.modules.customerservice.application.usecase.CreateSurveyUseCase;
import com.clinica.salud.modules.customerservice.application.usecase.GetComplaintUseCase;
import com.clinica.salud.modules.customerservice.application.usecase.ListComplaintsUseCase;
import com.clinica.salud.modules.customerservice.application.usecase.ListSurveysUseCase;
import com.clinica.salud.modules.customerservice.application.usecase.ResolveComplaintUseCase;
import com.clinica.salud.modules.customerservice.domain.model.ComplaintStatus;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ComplaintController {

    private final CreateComplaintUseCase createComplaintUseCase;
    private final ResolveComplaintUseCase resolveComplaintUseCase;
    private final CreateSurveyUseCase createSurveyUseCase;
    private final ListComplaintsUseCase listComplaintsUseCase;
    private final GetComplaintUseCase getComplaintUseCase;
    private final ListSurveysUseCase listSurveysUseCase;

    @PostMapping("/api/customer-service/complaints")
    @PreAuthorize("hasAuthority('ATENCION_CLIENTE_WRITE')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> create(
            @Valid @RequestBody CreateComplaintRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(createComplaintUseCase.execute(request)));
    }

    @GetMapping("/api/customer-service/complaints")
    @PreAuthorize("hasAuthority('ATENCION_CLIENTE_READ')")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> list(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID sedeId,
            @RequestParam(required = false) ComplaintStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(listComplaintsUseCase.execute(patientId, sedeId, status)));
    }

    @GetMapping("/api/customer-service/complaints/{id}")
    @PreAuthorize("hasAuthority('ATENCION_CLIENTE_READ')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(getComplaintUseCase.execute(id)));
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

    @GetMapping("/api/customer-service/surveys")
    @PreAuthorize("hasAuthority('ATENCION_CLIENTE_READ')")
    public ResponseEntity<ApiResponse<List<SurveyResponse>>> listSurveys(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID appointmentId) {
        return ResponseEntity.ok(ApiResponse.ok(listSurveysUseCase.execute(patientId, appointmentId)));
    }
}
