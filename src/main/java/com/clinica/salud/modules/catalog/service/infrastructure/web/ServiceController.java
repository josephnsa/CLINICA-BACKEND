package com.clinica.salud.modules.catalog.service.infrastructure.web;

import com.clinica.salud.modules.catalog.service.application.dto.CreateServiceRequest;
import com.clinica.salud.modules.catalog.service.application.dto.ServiceFilterRequest;
import com.clinica.salud.modules.catalog.service.application.dto.ServiceResponse;
import com.clinica.salud.modules.catalog.service.application.usecase.CreateServiceUseCase;
import com.clinica.salud.modules.catalog.service.application.usecase.DeactivateServiceUseCase;
import com.clinica.salud.modules.catalog.service.application.usecase.ListServicesUseCase;
import com.clinica.salud.modules.catalog.service.application.usecase.UpdateServiceUseCase;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/services")
@RequiredArgsConstructor
public class ServiceController {

    private final CreateServiceUseCase createServiceUseCase;
    private final UpdateServiceUseCase updateServiceUseCase;
    private final ListServicesUseCase listServicesUseCase;
    private final DeactivateServiceUseCase deactivateServiceUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<ServiceResponse>> create(
            @Valid @RequestBody CreateServiceRequest request) {
        ServiceResponse response = createServiceUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> list(
            @RequestParam(required = false) UUID specialtyId,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {
        ServiceFilterRequest filter = new ServiceFilterRequest(specialtyId, activeOnly);
        List<ServiceResponse> list = listServicesUseCase.execute(filter);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<ServiceResponse>> update(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        Integer durationMin = body.get("durationMin") != null
                ? ((Number) body.get("durationMin")).intValue()
                : null;
        BigDecimal price = body.get("price") != null
                ? new BigDecimal(body.get("price").toString())
                : null;
        ServiceResponse response = updateServiceUseCase.execute(id, durationMin, price);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        deactivateServiceUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}

