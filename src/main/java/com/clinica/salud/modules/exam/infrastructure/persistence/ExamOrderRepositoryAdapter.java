package com.clinica.salud.modules.exam.infrastructure.persistence;

import com.clinica.salud.modules.exam.domain.model.ExamOrder;
import com.clinica.salud.modules.exam.domain.port.ExamOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExamOrderRepositoryAdapter implements ExamOrderRepository {

    private final ExamOrderJpaRepository jpaRepository;
    private final ExamOrderMapper mapper;

    @Override
    public ExamOrder save(ExamOrder order) {
        ExamOrderEntity entity = mapper.toEntity(order);
        ExamOrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ExamOrder> findById(UUID id) {
        return jpaRepository.findWithItemsById(id).map(mapper::toDomain);
    }

    @Override
    public List<ExamOrder> findByPatient(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

