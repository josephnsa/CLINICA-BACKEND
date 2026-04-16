package com.clinica.salud.modules.dashboard.domain.port;

import com.clinica.salud.modules.dashboard.application.dto.*;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository {

    DashboardSummaryResponse getSummary(UUID sedeId, int year, int month);

    List<RevenueMonthResponse> getMonthlyRevenue(UUID sedeId, int year);

    YearlyBreakupResponse getYearlyBreakup(UUID sedeId);

    List<RecentTransactionResponse> getRecentTransactions(UUID sedeId, int limit);

    List<ServicePerformanceResponse> getServicePerformanceRaw(UUID sedeId, int month, int year);
}
