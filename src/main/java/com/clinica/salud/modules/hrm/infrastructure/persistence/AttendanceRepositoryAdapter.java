package com.clinica.salud.modules.hrm.infrastructure.persistence;

import com.clinica.salud.modules.hrm.domain.model.AttendanceRecord;
import com.clinica.salud.modules.hrm.domain.port.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AttendanceRepositoryAdapter implements AttendanceRepository {

    private final AttendanceJpaRepository jpaRepository;
    private final AttendanceMapper mapper;

    @Override
    public AttendanceRecord save(AttendanceRecord record) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(record)));
    }

    @Override
    public Optional<AttendanceRecord> findByEmployeeIdAndDate(UUID employeeId, LocalDate date) {
        return jpaRepository.findByEmployeeIdAndDate(employeeId, date).map(mapper::toDomain);
    }

    @Override
    public List<AttendanceRecord> findByEmployeeId(UUID employeeId) {
        return jpaRepository.findByEmployeeId(employeeId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> findByEmployeeIdAndDateBetween(UUID employeeId, LocalDate from, LocalDate to) {
        return jpaRepository.findByEmployeeIdAndDateBetween(employeeId, from, to).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> findBySedeIdAndDate(UUID sedeId, LocalDate date) {
        return jpaRepository.findBySedeIdAndDate(sedeId, date).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmployeeIdAndDate(UUID employeeId, LocalDate date) {
        return jpaRepository.existsByEmployeeIdAndDate(employeeId, date);
    }
}
