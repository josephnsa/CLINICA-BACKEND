package com.clinica.salud.modules.patients.application.usecase;

import com.clinica.salud.modules.patients.domain.model.PatientConsent;
import com.clinica.salud.modules.patients.domain.port.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateConsentUseCase {

    private final ConsentRepository consentRepository;

    @Transactional
    public PatientConsent execute(UUID patientId, String type, String fileUrl, UUID createdBy) {
        PatientConsent consent = PatientConsent.builder()
                .patientId(patientId)
                .type(type)
                .fileUrl(fileUrl)
                .signedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
        consent.validate();
        return consentRepository.save(consent);
    }

    public List<PatientConsent> getByPatient(UUID patientId) {
        return consentRepository.findByPatientId(patientId);
    }

    @Transactional
    public void delete(UUID consentId) {
        consentRepository.delete(consentId);
    }
}
