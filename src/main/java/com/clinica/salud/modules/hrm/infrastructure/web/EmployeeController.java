package com.clinica.salud.modules.hrm.infrastructure.web;

import com.clinica.salud.modules.hrm.application.dto.CreateEmployeeRequest;
import com.clinica.salud.modules.hrm.application.dto.EmployeeResponse;
import com.clinica.salud.modules.hrm.application.usecase.CreateEmployeeUseCase;
import com.clinica.salud.modules.hrm.application.usecase.DeactivateEmployeeUseCase;
import com.clinica.salud.modules.hrm.application.usecase.ListEmployeesUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hrm/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final ListEmployeesUseCase listEmployeesUseCase;
    private final DeactivateEmployeeUseCase deactivateEmployeeUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('RR_HH_WRITE')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = createEmployeeUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.ok("Empleado registrado exitosamente", response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RR_HH_READ')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> list(
            @RequestParam UUID sedeId,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        List<EmployeeResponse> employees = listEmployeesUseCase.execute(sedeId, activeOnly);
        return ResponseEntity.ok(ApiResponse.ok("Empleados obtenidos", employees));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RR_HH_WRITE')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        deactivateEmployeeUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok("Empleado desactivado", null));
    }
}
