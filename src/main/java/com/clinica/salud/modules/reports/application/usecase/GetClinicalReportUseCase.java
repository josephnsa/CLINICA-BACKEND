package com.clinica.salud.modules.reports.application.usecase;

import com.clinica.salud.modules.reports.application.dto.ClinicalReportResponse;
import com.clinica.salud.modules.reports.application.dto.ReportRequest;
import com.clinica.salud.modules.reports.domain.port.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetClinicalReportUseCase {

    private final ReportRepository reportRepository;

    public ClinicalReportResponse execute(ReportRequest request) {
        return reportRepository.getClinicalReport(request.getSedeId(), request.getDateFrom(), request.getDateTo());
    }
}
