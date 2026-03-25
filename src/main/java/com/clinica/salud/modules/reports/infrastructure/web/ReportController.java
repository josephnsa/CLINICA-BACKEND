package com.clinica.salud.modules.reports.infrastructure.web;

import com.clinica.salud.modules.reports.application.dto.FinancialReportResponse;
import com.clinica.salud.modules.reports.application.dto.InventoryReportResponse;
import com.clinica.salud.modules.reports.application.dto.OperationalReportResponse;
import com.clinica.salud.modules.reports.application.dto.ReportRequest;
import com.clinica.salud.modules.reports.application.usecase.GetFinancialReportUseCase;
import com.clinica.salud.modules.reports.application.usecase.GetInventoryReportUseCase;
import com.clinica.salud.modules.reports.application.usecase.GetOperationalReportUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GetOperationalReportUseCase operationalReportUseCase;
    private final GetFinancialReportUseCase financialReportUseCase;
    private final GetInventoryReportUseCase inventoryReportUseCase;

    @PostMapping("/operational")
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<OperationalReportResponse>> operational(
            @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(operationalReportUseCase.execute(request)));
    }

    @PostMapping("/financial")
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<FinancialReportResponse>> financial(
            @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(financialReportUseCase.execute(request)));
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<InventoryReportResponse>> inventory(@RequestParam UUID sedeId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryReportUseCase.execute(sedeId)));
    }
}
