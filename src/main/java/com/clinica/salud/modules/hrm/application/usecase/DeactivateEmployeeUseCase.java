package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.domain.model.Employee;
import com.clinica.salud.modules.hrm.domain.port.EmployeeRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeactivateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public void execute(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", employeeId.toString()));
        employee.deactivate();
        employeeRepository.save(employee);
    }
}
