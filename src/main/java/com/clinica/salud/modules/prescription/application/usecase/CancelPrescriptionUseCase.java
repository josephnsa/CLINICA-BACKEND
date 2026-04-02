package com.clinica.salud.modules.prescription.application.usecase;

import com.clinica.salud.modules.prescription.application.dto.PrescriptionItemResponse;
import com.clinica.salud.modules.prescription.application.dto.PrescriptionResponse;
import com.clinica.salud.modules.prescription.domain.model.Prescription;
import com.clinica.salud.modules.prescription.domain.model.PrescriptionItem;
import com.clinica.salud.modules.prescription.domain.port.PrescriptionRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelPrescriptionUseCase {

    private final PrescriptionRepository prescriptionRepository;

    @Transactional
    public PrescriptionResponse execute(UUID prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", prescriptionId.toString()));
        prescription.cancel();
        Prescription saved = prescriptionRepository.save(prescription);

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

    private List<PrescriptionItem> safeItems(Prescription prescription) {
        return prescription.getItems() == null ? List.of() : prescription.getItems();
    }
}
