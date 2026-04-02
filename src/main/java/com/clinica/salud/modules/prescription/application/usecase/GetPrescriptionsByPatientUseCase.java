package com.clinica.salud.modules.prescription.application.usecase;

import com.clinica.salud.modules.prescription.application.dto.PrescriptionItemResponse;
import com.clinica.salud.modules.prescription.application.dto.PrescriptionResponse;
import com.clinica.salud.modules.prescription.domain.model.Prescription;
import com.clinica.salud.modules.prescription.domain.model.PrescriptionItem;
import com.clinica.salud.modules.prescription.domain.port.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPrescriptionsByPatientUseCase {

    private final PrescriptionRepository prescriptionRepository;

    public List<PrescriptionResponse> execute(UUID patientId) {
        return prescriptionRepository.findByPatient(patientId).stream()
                .map(p -> new PrescriptionResponse(
                        p.getId(),
                        p.getPatientId(),
                        p.getDoctorId(),
                        p.getAppointmentId(),
                        p.getDiagnosisId(),
                        p.getNotes(),
                        p.getStatus(),
                        p.getCreatedAt(),
                        p.getCreatedBy(),
                        safeItems(p).stream()
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
                                .toList()
                ))
                .toList();
    }

    private List<PrescriptionItem> safeItems(Prescription prescription) {
        return prescription.getItems() == null ? List.of() : prescription.getItems();
    }
}

