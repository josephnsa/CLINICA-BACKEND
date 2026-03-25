package com.clinica.salud.modules.catalog.specialty.infrastructure.web;

import com.clinica.salud.modules.catalog.specialty.application.dto.CreateSpecialtyRequest;
import com.clinica.salud.modules.catalog.specialty.application.dto.SpecialtyResponse;
import com.clinica.salud.modules.catalog.specialty.application.usecase.CreateSpecialtyUseCase;
import com.clinica.salud.modules.catalog.specialty.application.usecase.DeactivateSpecialtyUseCase;
import com.clinica.salud.modules.catalog.specialty.application.usecase.ListSpecialtiesUseCase;
import com.clinica.salud.modules.catalog.specialty.application.usecase.UpdateSpecialtyUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final CreateSpecialtyUseCase createSpecialtyUseCase;
    private final UpdateSpecialtyUseCase updateSpecialtyUseCase;
    private final ListSpecialtiesUseCase listSpecialtiesUseCase;
    private final DeactivateSpecialtyUseCase deactivateSpecialtyUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> create(
            @Valid @RequestBody CreateSpecialtyRequest request) {
        SpecialtyResponse response = createSpecialtyUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> list() {
        List<SpecialtyResponse> list = listSpecialtiesUseCase.execute();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody Map<String, String> body) {
        String name = body.get("name");
        SpecialtyResponse response = updateSpecialtyUseCase.execute(id, name);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        deactivateSpecialtyUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}

