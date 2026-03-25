package com.clinica.salud.modules.agenda.domain.port;

import com.clinica.salud.modules.agenda.domain.model.AvailabilityBlock;
import com.clinica.salud.modules.agenda.domain.model.DoctorAvailabilityRule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AvailabilityRepository {

    DoctorAvailabilityRule saveRule(DoctorAvailabilityRule rule);

    List<DoctorAvailabilityRule> findRulesByDoctor(UUID doctorId);

    List<DoctorAvailabilityRule> findActiveRulesByDoctor(UUID doctorId);

    void deactivateRule(UUID ruleId);

    AvailabilityBlock saveBlock(AvailabilityBlock block);

    List<AvailabilityBlock> findBlocksByDoctor(UUID doctorId);

    List<AvailabilityBlock> findActiveBlocksByDoctorAndRange(UUID doctorId, LocalDateTime from, LocalDateTime to);

    void deleteBlock(UUID blockId);
}
