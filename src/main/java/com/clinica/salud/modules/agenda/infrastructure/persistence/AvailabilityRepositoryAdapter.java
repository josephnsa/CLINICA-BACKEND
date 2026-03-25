package com.clinica.salud.modules.agenda.infrastructure.persistence;

import com.clinica.salud.modules.agenda.domain.model.AvailabilityBlock;
import com.clinica.salud.modules.agenda.domain.model.DoctorAvailabilityRule;
import com.clinica.salud.modules.agenda.domain.port.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AvailabilityRepositoryAdapter implements AvailabilityRepository {

    private final DoctorAvailabilityJpaRepository ruleJpaRepo;
    private final AvailabilityBlockJpaRepository blockJpaRepo;
    private final AvailabilityMapper mapper;

    @Override
    public DoctorAvailabilityRule saveRule(DoctorAvailabilityRule rule) {
        DoctorAvailabilityEntity entity = mapper.ruleToEntity(rule);
        entity.setActive(rule.isActive());
        return mapper.ruleToDomain(ruleJpaRepo.save(entity));
    }

    @Override
    public List<DoctorAvailabilityRule> findRulesByDoctor(UUID doctorId) {
        return ruleJpaRepo.findByDoctorId(doctorId).stream()
                .map(mapper::ruleToDomain)
                .toList();
    }

    @Override
    public List<DoctorAvailabilityRule> findActiveRulesByDoctor(UUID doctorId) {
        return ruleJpaRepo.findByDoctorIdAndActiveTrue(doctorId).stream()
                .map(mapper::ruleToDomain)
                .toList();
    }

    @Override
    public void deactivateRule(UUID ruleId) {
        ruleJpaRepo.findById(ruleId).ifPresent(e -> {
            e.setActive(false);
            ruleJpaRepo.save(e);
        });
    }

    @Override
    public AvailabilityBlock saveBlock(AvailabilityBlock block) {
        return mapper.blockToDomain(blockJpaRepo.save(mapper.blockToEntity(block)));
    }

    @Override
    public List<AvailabilityBlock> findBlocksByDoctor(UUID doctorId) {
        return blockJpaRepo.findByDoctorId(doctorId).stream()
                .map(mapper::blockToDomain)
                .toList();
    }

    @Override
    public List<AvailabilityBlock> findActiveBlocksByDoctorAndRange(UUID doctorId, LocalDateTime from, LocalDateTime to) {
        return blockJpaRepo.findActiveByDoctorAndRange(doctorId, from, to).stream()
                .map(mapper::blockToDomain)
                .toList();
    }

    @Override
    public void deleteBlock(UUID blockId) {
        blockJpaRepo.deleteById(blockId);
    }
}
