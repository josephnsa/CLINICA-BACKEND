package com.clinica.salud.modules.billing.infrastructure.web;

import com.clinica.salud.modules.billing.application.dto.SunatStatusResponse;
import com.clinica.salud.modules.billing.application.usecase.GetSunatStatusUseCase;
import com.clinica.salud.modules.billing.application.usecase.SendToSunatUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SunatController {

    private final SendToSunatUseCase sendToSunatUseCase;
    private final GetSunatStatusUseCase getSunatStatusUseCase;

    @PostMapping("/api/invoices/{id}/send-sunat")
    @PreAuthorize("hasAuthority('FACTURACION_CREATE')")
    public ResponseEntity<ApiResponse<SunatStatusResponse>> sendToSunat(@PathVariable UUID id) {
        SunatStatusResponse response = sendToSunatUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/invoices/{id}/sunat-status")
    @PreAuthorize("hasAuthority('FACTURACION_READ')")
    public ResponseEntity<ApiResponse<SunatStatusResponse>> getSunatStatus(@PathVariable UUID id) {
        SunatStatusResponse response = getSunatStatusUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
