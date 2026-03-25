package com.clinica.salud.modules.patients.infrastructure.web;

import com.clinica.salud.modules.patients.application.dto.CreatePatientRequest;
import com.clinica.salud.modules.patients.application.dto.PatientResponse;
import com.clinica.salud.modules.patients.application.dto.PatientSearchRequest;
import com.clinica.salud.modules.patients.application.dto.UpdatePatientRequest;
import com.clinica.salud.modules.patients.application.usecase.CreatePatientUseCase;
import com.clinica.salud.modules.patients.application.usecase.GetPatientUseCase;
import com.clinica.salud.modules.patients.application.usecase.SearchPatientsUseCase;
import com.clinica.salud.modules.patients.application.usecase.UpdatePatientUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;
    private final UpdatePatientUseCase updatePatientUseCase;
    private final SearchPatientsUseCase searchPatientsUseCase;
    private final GetPatientUseCase getPatientUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody CreatePatientRequest request) {
        PatientResponse response = createPatientUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientRequest request) {
        PatientResponse response = updatePatientUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PatientResponse>>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String docNumber,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        PatientSearchRequest criteria = new PatientSearchRequest(query, docNumber, activeOnly);
        Page<PatientResponse> page = searchPatientsUseCase.execute(criteria, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getById(@PathVariable UUID id) {
        PatientResponse response = getPatientUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
