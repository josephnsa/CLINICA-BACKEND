package com.clinica.salud.modules.exam.domain.port;

import com.clinica.salud.modules.exam.domain.model.ExamOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExamOrderRepository {

    ExamOrder save(ExamOrder order);

    Optional<ExamOrder> findById(UUID id);

    List<ExamOrder> findByPatient(UUID patientId);
}

