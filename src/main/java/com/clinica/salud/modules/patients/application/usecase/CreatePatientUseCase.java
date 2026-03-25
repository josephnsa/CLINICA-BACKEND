package com.clinica.salud.modules.patients.application.usecase;

import com.clinica.salud.modules.patients.application.dto.CreatePatientRequest;
import com.clinica.salud.modules.patients.application.dto.PatientResponse;
import com.clinica.salud.modules.patients.domain.model.ClinicalProfile;
import com.clinica.salud.modules.patients.domain.model.Patient;
import com.clinica.salud.modules.patients.domain.port.ClinicalProfileRepository;
import com.clinica.salud.modules.patients.domain.port.PatientRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePatientUseCase {

    private final PatientRepository patientRepository;
    private final ClinicalProfileRepository clinicalProfileRepository;

    public PatientResponse execute(CreatePatientRequest req) {
        if (patientRepository.existsByDocNumber(req.docNumber())) {
            throw new BusinessRuleException("Ya existe un paciente con documento: " + req.docNumber());
        }
        LocalDateTime now = LocalDateTime.now();
        Patient patient = Patient.builder()
                .docType(req.docType())
                .docNumber(req.docNumber())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .birthDate(req.birthDate())
                .gender(req.gender())
                .email(req.email())
                .phone(req.phone())
                .address(req.address())
                .bloodType(req.bloodType())
                .emergencyName(req.emergencyName())
                .emergencyPhone(req.emergencyPhone())
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        patient = patientRepository.save(patient);

        ClinicalProfile profile = ClinicalProfile.builder()
                .patientId(patient.getId())
                .allergies(null)
                .personalHistory(null)
                .familyHistory(null)
                .surgicalHistory(null)
                .currentMeds(null)
                .updatedAt(now)
                .build();
        clinicalProfileRepository.save(profile);

        return toResponse(patient);
    }

    private static PatientResponse toResponse(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getDocType(),
                p.getDocNumber(),
                p.getFirstName(),
                p.getLastName(),
                p.getFullName(),
                p.getAge(),
                p.getBirthDate(),
                p.getGender(),
                p.getEmail(),
                p.getPhone(),
                p.getAddress(),
                p.getBloodType(),
                p.getEmergencyName(),
                p.getEmergencyPhone(),
                p.isActive(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
