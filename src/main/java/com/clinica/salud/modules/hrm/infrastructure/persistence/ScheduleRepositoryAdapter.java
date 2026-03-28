package com.clinica.salud.modules.hrm.infrastructure.persistence;

import com.clinica.salud.modules.hrm.domain.model.EmployeeSchedule;
import com.clinica.salud.modules.hrm.domain.port.ScheduleRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryAdapter implements ScheduleRepository {

    private final EmployeeScheduleJpaRepository jpaRepository;
    private final EmployeeScheduleMapper mapper;

    @Override
    public EmployeeSchedule save(EmployeeSchedule schedule) {
        EmployeeScheduleEntity entity = mapper.toEntity(schedule);
        entity.setActive(schedule.isActive());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<EmployeeSchedule> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<EmployeeSchedule> findActiveByEmployeeId(UUID employeeId) {
        return jpaRepository.findByEmployeeIdAndIsActiveTrue(employeeId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeSchedule> findAllByEmployeeId(UUID employeeId) {
        return jpaRepository.findByEmployeeId(employeeId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsActiveForDay(UUID employeeId, int dayOfWeek) {
        return jpaRepository.existsByEmployeeIdAndDayOfWeekAndIsActiveTrue(employeeId, dayOfWeek);
    }

    @Override
    public void deactivate(UUID id) {
        EmployeeScheduleEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario", id.toString()));
        entity.setActive(false);
        jpaRepository.save(entity);
    }
}
