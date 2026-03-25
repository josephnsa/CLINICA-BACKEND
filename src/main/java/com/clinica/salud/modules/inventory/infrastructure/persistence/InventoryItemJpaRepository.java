package com.clinica.salud.modules.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryItemJpaRepository extends JpaRepository<InventoryItemEntity, UUID> {

    Optional<InventoryItemEntity> findByMedicationIdAndSedeId(UUID medicationId, UUID sedeId);

    @Query("""
            select i
            from InventoryItemEntity i
            where i.sedeId = :sedeId and i.stock <= i.minStock
            """)
    List<InventoryItemEntity> findLowStock(@Param("sedeId") UUID sedeId);

    @Query("""
            select min(m.expiryDate)
            from InventoryMovementEntity m
            where m.item.id = :itemId
              and m.expiryDate is not null
              and m.expiryDate >= :today
            """)
    LocalDate findNearestExpiryDate(@Param("itemId") UUID itemId, @Param("today") LocalDate today);
}

