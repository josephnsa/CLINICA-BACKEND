package com.clinica.salud.modules.agenda.infrastructure.web;

import com.clinica.salud.modules.agenda.application.dto.AddAvailabilityBlockRequest;
import com.clinica.salud.modules.agenda.application.dto.AvailabilityBlockResponse;
import com.clinica.salud.modules.agenda.application.dto.AvailabilityRuleResponse;
import com.clinica.salud.modules.agenda.application.dto.SetAvailabilityRuleRequest;
import com.clinica.salud.modules.agenda.application.usecase.AddAvailabilityBlockUseCase;
import com.clinica.salud.modules.agenda.application.usecase.SetDoctorAvailabilityUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final SetDoctorAvailabilityUseCase setAvailabilityUseCase;
    private final AddAvailabilityBlockUseCase addBlockUseCase;

    // ── Reglas semanales ──────────────────────────────────────────

    @PostMapping("/rules")
    @PreAuthorize("hasAuthority('agenda:availability')")
    public ResponseEntity<ApiResponse<AvailabilityRuleResponse>> createRule(
            @Valid @RequestBody SetAvailabilityRuleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(setAvailabilityUseCase.execute(request)));
    }

    @GetMapping("/rules")
    @PreAuthorize("hasAuthority('agenda:availability')")
    public ResponseEntity<ApiResponse<List<AvailabilityRuleResponse>>> getRules(
            @RequestParam UUID doctorId) {
        return ResponseEntity.ok(ApiResponse.ok(setAvailabilityUseCase.getRulesByDoctor(doctorId)));
    }

    @DeleteMapping("/rules/{ruleId}")
    @PreAuthorize("hasAuthority('agenda:availability')")
    public ResponseEntity<ApiResponse<Void>> deactivateRule(@PathVariable UUID ruleId) {
        setAvailabilityUseCase.deactivateRule(ruleId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ── Bloqueos (vacaciones, feriados) ──────────────────────────

    @PostMapping("/blocks")
    @PreAuthorize("hasAuthority('agenda:availability')")
    public ResponseEntity<ApiResponse<AvailabilityBlockResponse>> createBlock(
            @Valid @RequestBody AddAvailabilityBlockRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(addBlockUseCase.execute(request)));
    }

    @GetMapping("/blocks")
    @PreAuthorize("hasAuthority('agenda:availability')")
    public ResponseEntity<ApiResponse<List<AvailabilityBlockResponse>>> getBlocks(
            @RequestParam UUID doctorId) {
        return ResponseEntity.ok(ApiResponse.ok(addBlockUseCase.getBlocksByDoctor(doctorId)));
    }

    @DeleteMapping("/blocks/{blockId}")
    @PreAuthorize("hasAuthority('agenda:availability')")
    public ResponseEntity<ApiResponse<Void>> deleteBlock(@PathVariable UUID blockId) {
        addBlockUseCase.deleteBlock(blockId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
