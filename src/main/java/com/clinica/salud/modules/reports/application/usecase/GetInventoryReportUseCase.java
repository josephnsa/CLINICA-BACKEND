package com.clinica.salud.modules.reports.application.usecase;

import com.clinica.salud.modules.reports.application.dto.InventoryReportResponse;
import com.clinica.salud.modules.reports.domain.port.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetInventoryReportUseCase {

    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public InventoryReportResponse execute(UUID sedeId) {
        return reportRepository.getInventoryReport(sedeId, LocalDate.now());
    }
}
