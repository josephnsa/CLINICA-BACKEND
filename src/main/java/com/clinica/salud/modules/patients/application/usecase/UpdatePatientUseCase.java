package com.clinica.salud.modules.patients.application.usecase;

import com.clinica.salud.modules.patients.application.dto.PatientResponse;
import com.clinica.salud.modules.patients.application.dto.UpdatePatientRequest;
import com.clinica.salud.modules.patients.domain.model.Patient;
import com.clinica.salud.modules.patients.domain.port.PatientRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdatePatientUseCase {

    private final PatientRepository patientRepository;

    public PatientResponse execute(UUID id, UpdatePatientRequest req) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id.toString()));

        if (req.docType() != null) patient.setDocType(req.docType());
        if (req.docNumber() != null) patient.setDocNumber(req.docNumber());
        if (req.firstName() != null) patient.setFirstName(req.firstName());
        if (req.lastName() != null) patient.setLastName(req.lastName());
        if (req.birthDate() != null) patient.setBirthDate(req.birthDate());
        if (req.gender() != null) patient.setGender(req.gender());
        if (req.email() != null) patient.setEmail(req.email());
        if (req.phone() != null) patient.setPhone(req.phone());
        if (req.address() != null) patient.setAddress(req.address());
        if (req.bloodType() != null) patient.setBloodType(req.bloodType());
        if (req.emergencyName() != null) patient.setEmergencyName(req.emergencyName());
        if (req.emergencyPhone() != null) patient.setEmergencyPhone(req.emergencyPhone());
        if (req.isActive() != null) patient.setActive(req.isActive());

        patient.setUpdatedAt(LocalDateTime.now());
        patient = patientRepository.save(patient);

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
