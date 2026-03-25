package com.clinica.salud.modules.billing.domain.port;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Obtiene el precio unitario de un servicio en una sede según el tarifario.
 */
public interface GetTariffPricePort {

    Optional<BigDecimal> getUnitPrice(UUID serviceId, UUID sedeId);
}
