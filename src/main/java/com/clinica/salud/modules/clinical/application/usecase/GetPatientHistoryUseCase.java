package com.clinica.salud.modules.clinical.application.usecase;

import com.clinica.salud.modules.clinical.application.dto.ClinicalNoteResponse;
import com.clinica.salud.modules.clinical.domain.model.ClinicalNote;
import com.clinica.salud.modules.clinical.domain.port.ClinicalNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPatientHistoryUseCase {

    private final ClinicalNoteRepository clinicalNoteRepository;

    public Page<ClinicalNoteResponse> execute(UUID patientId, Pageable pageable) {
        return clinicalNoteRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable)
                .map(this::toResponse);
    }

    private ClinicalNoteResponse toResponse(ClinicalNote n) {
        return new ClinicalNoteResponse(
                n.getId(),
                n.getAppointmentId(),
                n.getPatientId(),
                n.getDoctorId(),
                n.getReason(),
                n.getPhysicalExam(),
                n.getDiagnosisCode(),
                n.getDiagnosisDesc(),
                n.getTreatmentPlan(),
                n.getCreatedAt(),
                null,
                null
        );
    }
}
