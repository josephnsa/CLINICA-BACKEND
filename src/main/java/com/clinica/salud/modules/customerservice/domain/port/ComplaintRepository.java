package com.clinica.salud.modules.customerservice.domain.port;

import com.clinica.salud.modules.customerservice.domain.model.Complaint;
import com.clinica.salud.modules.customerservice.domain.model.ComplaintStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComplaintRepository {

    Complaint save(Complaint complaint);

    Optional<Complaint> findById(UUID id);

    List<Complaint> findAll();

    List<Complaint> findBySedeId(UUID sedeId);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByPatientId(UUID patientId);
}
