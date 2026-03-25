package com.clinica.salud.modules.reports.application.usecase;

import com.clinica.salud.modules.reports.application.dto.FinancialReportResponse;
import com.clinica.salud.modules.reports.application.dto.ReportRequest;
import com.clinica.salud.modules.reports.domain.port.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetFinancialReportUseCase {

    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public FinancialReportResponse execute(ReportRequest request) {
        request.validate();
        return reportRepository.getFinancialReport(
                request.getSedeId(), request.getDateFrom(), request.getDateTo());
    }
}
