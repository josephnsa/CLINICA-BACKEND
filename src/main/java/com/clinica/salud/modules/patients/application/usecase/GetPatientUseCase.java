package com.clinica.salud.modules.patients.application.usecase;

import com.clinica.salud.modules.patients.application.dto.PatientResponse;
import com.clinica.salud.modules.patients.domain.model.Patient;
import com.clinica.salud.modules.patients.domain.port.PatientRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPatientUseCase {

    private final PatientRepository patientRepository;

    public PatientResponse execute(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id.toString()));
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
