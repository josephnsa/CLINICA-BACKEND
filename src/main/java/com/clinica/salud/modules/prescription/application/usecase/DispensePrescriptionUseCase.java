package com.clinica.salud.modules.prescription.application.usecase;

import com.clinica.salud.modules.prescription.application.dto.PrescriptionItemResponse;
import com.clinica.salud.modules.prescription.application.dto.PrescriptionResponse;
import com.clinica.salud.modules.prescription.domain.model.Prescription;
import com.clinica.salud.modules.prescription.domain.model.PrescriptionItem;
import com.clinica.salud.modules.prescription.domain.model.PrescriptionStatus;
import com.clinica.salud.modules.prescription.domain.port.PrescriptionRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispensePrescriptionUseCase {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionResponse execute(UUID prescriptionId, UUID userId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", prescriptionId.toString()));

        List<PrescriptionItem> updatedItems = prescription.getItems().stream()
                .map(i -> {
                    i.setDispensed(true);
                    return i;
                })
                .toList();

        prescription.setItems(updatedItems);
        prescription.setStatus(PrescriptionStatus.COMPLETED);

        Prescription saved = prescriptionRepository.save(prescription);

        List<PrescriptionItemResponse> itemResponses = saved.getItems().stream()
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
}

