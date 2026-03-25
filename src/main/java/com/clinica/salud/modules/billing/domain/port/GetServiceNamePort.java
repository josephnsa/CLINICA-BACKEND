package com.clinica.salud.modules.billing.domain.port;

import java.util.Optional;
import java.util.UUID;

/**
 * Obtiene el nombre/descripción de un servicio para ítems de factura.
 */
public interface GetServiceNamePort {

    Optional<String> getServiceName(UUID serviceId);
}
