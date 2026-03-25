package com.clinica.salud.modules.hrm.domain.port;

import com.clinica.salud.modules.hrm.domain.model.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(UUID id);

    List<Employee> findBySedeId(UUID sedeId);

    List<Employee> findBySpecialtyId(UUID specialtyId);

    List<Employee> findActiveBySedeId(UUID sedeId);

    boolean existsByDocNumber(String docNumber);

    boolean existsByLicenseNumber(String licenseNumber);
}
