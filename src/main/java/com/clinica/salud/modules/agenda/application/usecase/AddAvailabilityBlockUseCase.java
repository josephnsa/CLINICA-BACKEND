package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.application.dto.AddAvailabilityBlockRequest;
import com.clinica.salud.modules.agenda.application.dto.AvailabilityBlockResponse;
import com.clinica.salud.modules.agenda.domain.model.AvailabilityBlock;
import com.clinica.salud.modules.agenda.domain.port.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddAvailabilityBlockUseCase {

    private final AvailabilityRepository availabilityRepository;

    @Transactional
    public AvailabilityBlockResponse execute(AddAvailabilityBlockRequest request) {
        AvailabilityBlock block = AvailabilityBlock.builder()
                .doctorId(request.doctorId())
                .sedeId(request.sedeId())
                .startDt(request.startDt())
                .endDt(request.endDt())
                .reason(request.reason())
                .build();
        block.validate();
        return toResponse(availabilityRepository.saveBlock(block));
    }

    public List<AvailabilityBlockResponse> getBlocksByDoctor(UUID doctorId) {
        return availabilityRepository.findBlocksByDoctor(doctorId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteBlock(UUID blockId) {
        availabilityRepository.deleteBlock(blockId);
    }

    private AvailabilityBlockResponse toResponse(AvailabilityBlock block) {
        return new AvailabilityBlockResponse(
                block.getId(),
                block.getDoctorId(),
                block.getSedeId(),
                block.getStartDt(),
                block.getEndDt(),
                block.getReason()
        );
    }
}
