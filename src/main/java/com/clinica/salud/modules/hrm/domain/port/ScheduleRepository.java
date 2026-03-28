package com.clinica.salud.modules.hrm.domain.port;

import com.clinica.salud.modules.hrm.domain.model.EmployeeSchedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository {

    EmployeeSchedule save(EmployeeSchedule schedule);

    Optional<EmployeeSchedule> findById(UUID id);

    List<EmployeeSchedule> findActiveByEmployeeId(UUID employeeId);

    List<EmployeeSchedule> findAllByEmployeeId(UUID employeeId);

    boolean existsActiveForDay(UUID employeeId, int dayOfWeek);

    void deactivate(UUID id);
}
