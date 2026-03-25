package com.clinica.salud.modules.hrm.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, UUID> {

    List<EmployeeEntity> findBySedeId(UUID sedeId);

    List<EmployeeEntity> findBySedeIdAndIsActiveTrue(UUID sedeId);

    List<EmployeeEntity> findBySpecialtyId(UUID specialtyId);

    boolean existsByDocNumber(String docNumber);

    boolean existsByLicenseNumber(String licenseNumber);
}
