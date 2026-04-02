package com.clinica.salud.modules.catalog.medication.infrastructure.web;

import com.clinica.salud.modules.catalog.medication.application.dto.CreateMedicationRequest;
import com.clinica.salud.modules.catalog.medication.application.dto.MedicationResponse;
import com.clinica.salud.modules.catalog.medication.application.dto.UpdateMedicationRequest;
import com.clinica.salud.modules.catalog.medication.application.usecase.CreateMedicationUseCase;
import com.clinica.salud.modules.catalog.medication.application.usecase.DeactivateMedicationUseCase;
import com.clinica.salud.modules.catalog.medication.application.usecase.SearchMedicationsUseCase;
import com.clinica.salud.modules.catalog.medication.application.usecase.UpdateMedicationUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final CreateMedicationUseCase createMedicationUseCase;
    private final UpdateMedicationUseCase updateMedicationUseCase;
    private final SearchMedicationsUseCase searchMedicationsUseCase;
    private final DeactivateMedicationUseCase deactivateMedicationUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<MedicationResponse>> create(
            @Valid @RequestBody CreateMedicationRequest request) {
        MedicationResponse response = createMedicationUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<Page<MedicationResponse>>> search(
            @RequestParam(required = false) String q,
            @RequestParam(name = "activeOnly", defaultValue = "true") boolean activeOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MedicationResponse> page =
                searchMedicationsUseCase.execute(q != null ? q : "", activeOnly, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<MedicationResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateMedicationRequest request) {
        MedicationResponse response = updateMedicationUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        deactivateMedicationUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}

