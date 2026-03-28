package com.clinica.salud.modules.hrm.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeScheduleJpaRepository extends JpaRepository<EmployeeScheduleEntity, UUID> {

    List<EmployeeScheduleEntity> findByEmployeeIdAndIsActiveTrue(UUID employeeId);

    List<EmployeeScheduleEntity> findByEmployeeId(UUID employeeId);

    boolean existsByEmployeeIdAndDayOfWeekAndIsActiveTrue(UUID employeeId, int dayOfWeek);
}
