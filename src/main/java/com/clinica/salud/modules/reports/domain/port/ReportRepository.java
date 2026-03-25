package com.clinica.salud.modules.reports.domain.port;

import com.clinica.salud.modules.reports.application.dto.FinancialReportResponse;
import com.clinica.salud.modules.reports.application.dto.InventoryReportResponse;
import com.clinica.salud.modules.reports.application.dto.OperationalReportResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportRepository {

    OperationalReportResponse getOperationalReport(UUID sedeId, LocalDate from, LocalDate to);

    FinancialReportResponse getFinancialReport(UUID sedeId, LocalDate from, LocalDate to);

    InventoryReportResponse getInventoryReport(UUID sedeId, LocalDate reportDate);
}
