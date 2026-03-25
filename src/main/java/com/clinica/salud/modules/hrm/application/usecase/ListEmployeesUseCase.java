package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.application.dto.EmployeeResponse;
import com.clinica.salud.modules.hrm.domain.model.Employee;
import com.clinica.salud.modules.hrm.domain.port.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListEmployeesUseCase {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> execute(UUID sedeId, boolean activeOnly) {
        List<Employee> employees = activeOnly
                ? employeeRepository.findActiveBySedeId(sedeId)
                : employeeRepository.findBySedeId(sedeId);
        return employees.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .fullName(e.getFullName())
                .docType(e.getDocType())
                .docNumber(e.getDocNumber())
                .licenseNumber(e.getLicenseNumber())
                .position(e.getPosition())
                .sedeId(e.getSedeId())
                .specialtyId(e.getSpecialtyId())
                .hireDate(e.getHireDate())
                .seniority(e.getSeniority())
                .isActive(e.isActive())
                .isLicensed(e.isLicensed())
                .phone(e.getPhone())
                .email(e.getEmail())
                .build();
    }
}
