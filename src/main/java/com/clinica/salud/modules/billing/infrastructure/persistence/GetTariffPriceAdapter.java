package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.port.GetTariffPricePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetTariffPriceAdapter implements GetTariffPricePort {

    private final TariffJpaRepository tariffJpaRepository;

    @Override
    public Optional<BigDecimal> getUnitPrice(UUID serviceId, UUID sedeId) {
        return tariffJpaRepository.findByServiceIdAndSedeId(serviceId, sedeId)
                .map(TariffEntity::getPrice);
    }
}
