package com.clinica.salud.modules.clinical.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cie10_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cie10Entity {

    @Id
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;
}
