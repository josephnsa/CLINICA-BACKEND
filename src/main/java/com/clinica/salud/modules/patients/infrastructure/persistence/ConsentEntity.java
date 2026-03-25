package com.clinica.salud.modules.patients.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_consents")
@Data
public class ConsentEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "signed_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime signedAt;

    @Column(name = "created_by")
    private UUID createdBy;
}
