package com.clinica.salud.modules.hrm.infrastructure.persistence;

import com.clinica.salud.modules.hrm.domain.model.Employee;
import com.clinica.salud.modules.hrm.domain.port.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryAdapter implements EmployeeRepository {

    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeMapper mapper;

    @Override
    public Employee save(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        entity.setActive(employee.isActive());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Employee> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Employee> findBySedeId(UUID sedeId) {
        return jpaRepository.findBySedeId(sedeId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Employee> findBySpecialtyId(UUID specialtyId) {
        return jpaRepository.findBySpecialtyId(specialtyId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Employee> findActiveBySedeId(UUID sedeId) {
        return jpaRepository.findBySedeIdAndIsActiveTrue(sedeId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Employee> findAllActive() {
        return jpaRepository.findByIsActiveTrue().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByDocNumber(String docNumber) {
        return jpaRepository.existsByDocNumber(docNumber);
    }

    @Override
    public boolean existsByLicenseNumber(String licenseNumber) {
        return jpaRepository.existsByLicenseNumber(licenseNumber);
    }
}
