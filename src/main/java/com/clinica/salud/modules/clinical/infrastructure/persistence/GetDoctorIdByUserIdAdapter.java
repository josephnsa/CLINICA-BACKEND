package com.clinica.salud.modules.clinical.infrastructure.persistence;

import com.clinica.salud.modules.clinical.domain.port.GetDoctorIdByUserIdPort;
import com.clinica.salud.modules.agenda.infrastructure.persistence.DoctorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetDoctorIdByUserIdAdapter implements GetDoctorIdByUserIdPort {

    private final DoctorJpaRepository doctorJpaRepository;

    @Override
    public Optional<UUID> getDoctorIdByUserId(UUID userId) {
        return doctorJpaRepository.findByUserId(userId).map(doctor -> doctor.getId());
    }
}
