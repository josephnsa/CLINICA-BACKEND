package com.clinica.salud.modules.inventory.infrastructure.persistence;

import com.clinica.salud.modules.inventory.domain.model.InventoryMovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_movements")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItemEntity item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private InventoryMovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "lot_number", length = 50)
    private String lotNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(length = 200)
    private String reason;

    @Column(length = 100)
    private String reference;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}

