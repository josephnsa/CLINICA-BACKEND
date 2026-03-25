package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.port.GetServiceNamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetServiceNameAdapter implements GetServiceNamePort {

    private final ServiceJpaRepository serviceJpaRepository;

    @Override
    public Optional<String> getServiceName(UUID serviceId) {
        return serviceJpaRepository.findById(serviceId).map(ServiceEntity::getName);
    }
}
