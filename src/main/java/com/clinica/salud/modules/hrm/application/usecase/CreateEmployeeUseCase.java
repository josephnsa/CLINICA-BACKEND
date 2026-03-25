package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.application.dto.CreateEmployeeRequest;
import com.clinica.salud.modules.hrm.application.dto.EmployeeResponse;
import com.clinica.salud.modules.hrm.domain.model.Employee;
import com.clinica.salud.modules.hrm.domain.port.EmployeeRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeResponse execute(CreateEmployeeRequest request) {
        // Validar unicidad del documento
        if (request.getDocNumber() != null && employeeRepository.existsByDocNumber(request.getDocNumber())) {
            throw new BusinessRuleException("Ya existe un empleado con el documento: " + request.getDocNumber());
        }
        // Validar unicidad de colegiatura
        if (request.getLicenseNumber() != null && !request.getLicenseNumber().isBlank()
                && employeeRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BusinessRuleException("Ya existe un profesional con la colegiatura: " + request.getLicenseNumber());
        }

        Employee employee = Employee.builder()
                .userId(request.getUserId())
                .sedeId(request.getSedeId())
                .specialtyId(request.getSpecialtyId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .docType(request.getDocType())
                .docNumber(request.getDocNumber())
                .licenseNumber(request.getLicenseNumber())
                .position(request.getPosition())
                .hireDate(request.getHireDate())
                .isActive(true)
                .phone(request.getPhone())
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        employee.validate();
        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
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
