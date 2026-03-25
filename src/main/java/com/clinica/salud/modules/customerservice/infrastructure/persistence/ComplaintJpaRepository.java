package com.clinica.salud.modules.customerservice.infrastructure.persistence;

import com.clinica.salud.modules.customerservice.domain.model.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComplaintJpaRepository extends JpaRepository<ComplaintEntity, UUID> {

    List<ComplaintEntity> findBySedeId(UUID sedeId);

    List<ComplaintEntity> findByStatus(ComplaintStatus status);

    List<ComplaintEntity> findByPatientId(UUID patientId);
}
