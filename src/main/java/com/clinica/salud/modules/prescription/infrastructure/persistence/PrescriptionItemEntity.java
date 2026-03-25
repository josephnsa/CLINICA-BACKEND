package com.clinica.salud.modules.prescription.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "prescription_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private PrescriptionEntity prescription;

    @Column(name = "medication_id", nullable = false)
    private UUID medicationId;

    @Column(nullable = false, length = 100)
    private String dose;

    @Column(nullable = false, length = 100)
    private String frequency;

    @Column(length = 100)
    private String duration;

    @Column(length = 50)
    private String route;

    @Column(length = 500)
    private String instructions;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "is_dispensed", nullable = false)
    private boolean dispensed;

    @Column(name = "dispensed_at")
    private OffsetDateTime dispensedAt;

    @Column(name = "dispensed_by")
    private UUID dispensedBy;
}

