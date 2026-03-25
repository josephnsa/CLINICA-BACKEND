package com.clinica.salud.modules.clinical.domain.port;

/**
 * Verifica existencia de códigos CIE-10.
 */
public interface Cie10ExistsPort {

    boolean existsByCode(String code);
}
