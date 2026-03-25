package com.clinica.salud.modules.exam.application.usecase;

import com.clinica.salud.modules.exam.application.dto.CreateExamOrderRequest;
import com.clinica.salud.modules.exam.application.dto.ExamOrderItemResponse;
import com.clinica.salud.modules.exam.application.dto.ExamOrderResponse;
import com.clinica.salud.modules.exam.domain.model.ExamOrder;
import com.clinica.salud.modules.exam.domain.model.ExamOrderItem;
import com.clinica.salud.modules.exam.domain.model.ExamOrderStatus;
import com.clinica.salud.modules.exam.domain.port.ExamOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateExamOrderUseCase {

    private final ExamOrderRepository examOrderRepository;

    public ExamOrderResponse execute(CreateExamOrderRequest request, UUID userId) {
        List<ExamOrderItem> items = request.items().stream()
                .map(i -> ExamOrderItem.builder()
                        .id(null)
                        .serviceId(i.serviceId())
                        .status(ExamOrderStatus.PENDIENTE)
                        .resultText(null)
                        .resultAt(null)
                        .resultBy(null)
                        .build())
                .toList();

        ExamOrder order = ExamOrder.builder()
                .id(null)
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .appointmentId(request.appointmentId())
                .status(ExamOrderStatus.PENDIENTE)
                .notes(request.notes())
                .createdAt(OffsetDateTime.now())
                .createdBy(userId)
                .items(items)
                .build();

        ExamOrder saved = examOrderRepository.save(order);

        List<ExamOrderItemResponse> itemResponses = saved.getItems().stream()
                .map(i -> new ExamOrderItemResponse(
                        i.getId(),
                        i.getServiceId(),
                        i.getStatus(),
                        i.getResultText(),
                        i.getResultAt(),
                        i.getResultBy()
                ))
                .toList();

        return new ExamOrderResponse(
                saved.getId(),
                saved.getPatientId(),
                saved.getDoctorId(),
                saved.getAppointmentId(),
                saved.getStatus(),
                saved.getNotes(),
                saved.getCreatedAt(),
                saved.getCreatedBy(),
                itemResponses
        );
    }
}

