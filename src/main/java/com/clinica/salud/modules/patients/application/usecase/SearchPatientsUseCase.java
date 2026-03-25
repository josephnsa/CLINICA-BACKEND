package com.clinica.salud.modules.patients.application.usecase;

import com.clinica.salud.modules.patients.application.dto.PatientResponse;
import com.clinica.salud.modules.patients.application.dto.PatientSearchRequest;
import com.clinica.salud.modules.patients.domain.model.Patient;
import com.clinica.salud.modules.patients.domain.port.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchPatientsUseCase {

    private final PatientRepository patientRepository;

    public Page<PatientResponse> execute(PatientSearchRequest criteria, Pageable pageable) {
        Page<Patient> page = patientRepository.search(
                criteria.query(),
                criteria.docNumber(),
                criteria.activeOnly(),
                pageable
        );
        return page.map(this::toResponse);
    }

    private PatientResponse toResponse(Patient p) {
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
