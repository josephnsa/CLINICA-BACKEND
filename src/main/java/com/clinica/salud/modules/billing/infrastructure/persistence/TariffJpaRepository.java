package com.clinica.salud.modules.billing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TariffJpaRepository extends JpaRepository<TariffEntity, UUID> {

    @Query("SELECT t FROM TariffEntity t WHERE t.serviceId = :serviceId AND t.sedeId = :sedeId")
    Optional<TariffEntity> findByServiceIdAndSedeId(@Param("serviceId") UUID serviceId, @Param("sedeId") UUID sedeId);
}
