package com.clinica.salud.modules.clinical.infrastructure.persistence;

import com.clinica.salud.modules.clinical.domain.port.Cie10ExistsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Cie10ExistsAdapter implements Cie10ExistsPort {

    private final Cie10JpaRepository cie10JpaRepository;

    @Override
    public boolean existsByCode(String code) {
        return cie10JpaRepository.existsByCode(code);
    }
}
