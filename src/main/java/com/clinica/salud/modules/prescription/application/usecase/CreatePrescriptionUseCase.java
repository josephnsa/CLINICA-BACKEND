package com.clinica.salud.modules.prescription.application.usecase;

import com.clinica.salud.modules.prescription.application.dto.CreatePrescriptionRequest;
import com.clinica.salud.modules.prescription.application.dto.PrescriptionItemResponse;
import com.clinica.salud.modules.prescription.application.dto.PrescriptionResponse;
import com.clinica.salud.modules.prescription.domain.model.Prescription;
import com.clinica.salud.modules.prescription.domain.model.PrescriptionItem;
import com.clinica.salud.modules.prescription.domain.model.PrescriptionStatus;
import com.clinica.salud.modules.prescription.domain.port.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePrescriptionUseCase {

    private final PrescriptionRepository prescriptionRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public PrescriptionResponse execute(CreatePrescriptionRequest request, UUID userId) {
        List<PrescriptionItem> items = request.items().stream()
                .map(i -> PrescriptionItem.builder()
                        .id(null)
                        .medicationId(i.medicationId())
                        .dose(i.dose())
                        .frequency(i.frequency())
                        .duration(i.duration())
                        .route(i.route())
                        .instructions(i.instructions())
                        .quantity(i.quantity())
                        .dispensed(false)
                        .build())
                .toList();

        Prescription prescription = Prescription.builder()
                .id(null)
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .appointmentId(request.appointmentId())
                .diagnosisId(request.diagnosisId())
                .notes(request.notes())
                .status(PrescriptionStatus.ACTIVE)
                .createdAt(OffsetDateTime.now())
                .createdBy(userId)
                .items(items)
                .build();

        prescription.validate();
        Prescription saved = prescriptionRepository.save(prescription);
        registerKardexOnPrescribe(saved, userId);

        List<PrescriptionItemResponse> itemResponses = safeItems(saved).stream()
                .map(i -> new PrescriptionItemResponse(
                        i.getId(),
                        i.getMedicationId(),
                        i.getDose(),
                        i.getFrequency(),
                        i.getDuration(),
                        i.getRoute(),
                        i.getInstructions(),
                        i.getQuantity(),
                        i.isDispensed()
                ))
                .toList();

        return new PrescriptionResponse(
                saved.getId(),
                saved.getPatientId(),
                saved.getDoctorId(),
                saved.getAppointmentId(),
                saved.getDiagnosisId(),
                saved.getNotes(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getCreatedBy(),
                itemResponses
        );
    }

    private void registerKardexOnPrescribe(Prescription prescription, UUID userId) {
        for (PrescriptionItem item : safeItems(prescription)) {
            jdbcTemplate.update("""
                INSERT INTO medication_kardex
                    (patient_id, prescription_id, medication_id, action, quantity, notes, recorded_by)
                VALUES (?, ?, ?, 'PRESCRIBED', ?, ?, ?)
                """,
                prescription.getPatientId(),
                prescription.getId(),
                item.getMedicationId(),
                item.getQuantity(),
                prescription.getNotes(),
                userId
            );
        }
    }

    private List<PrescriptionItem> safeItems(Prescription prescription) {
        return prescription.getItems() == null ? List.of() : prescription.getItems();
    }
}

