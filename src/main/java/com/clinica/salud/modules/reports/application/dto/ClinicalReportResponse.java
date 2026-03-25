package com.clinica.salud.modules.reports.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ClinicalReportResponse(
        UUID sedeId,
        LocalDate from,
        LocalDate to,
        long totalConsultations,
        long totalExamOrders,
        long totalPrescriptions,
        List<DiagnosisFrequency> topDiagnoses,
        List<ServiceFrequency> topServices,
        List<MedicationFrequency> topMedications
) {
    public record DiagnosisFrequency(String code, String description, long count) {}
    public record ServiceFrequency(String serviceName, long count) {}
    public record MedicationFrequency(String medicationName, long count) {}
}
