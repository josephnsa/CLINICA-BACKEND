package com.clinica.salud.modules.clinical.domain.port;

import java.util.Optional;
import java.util.UUID;

/**
 * Resuelve el ID del doctor a partir del ID del usuario autenticado.
 */
public interface GetDoctorIdByUserIdPort {

    Optional<UUID> getDoctorIdByUserId(UUID userId);
}
