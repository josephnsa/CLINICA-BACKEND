package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.application.dto.AvailabilityRequest;
import com.clinica.salud.modules.agenda.application.dto.TimeSlotDto;
import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.domain.model.AppointmentStatus;
import com.clinica.salud.modules.agenda.domain.port.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAvailabilityUseCase {

    private static final int SLOT_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;

    /**
     * Obtiene slots de 30 minutos para el médico en la sede y fecha indicados.
     * Horario laboral: 08:00 - 18:00. Marca como no disponible los ya ocupados por citas.
     */
    public List<TimeSlotDto> execute(AvailabilityRequest req) {
        LocalDateTime dayStart = req.date().atTime(LocalTime.MIN).plusHours(8);
        LocalDateTime dayEnd = req.date().atTime(LocalTime.MIN).plusHours(18);

        List<Appointment> existing = appointmentRepository.findByDoctorAndDate(req.doctorId(), req.date());

        List<TimeSlotDto> slots = new ArrayList<>();
        LocalDateTime slotStart = dayStart;
        while (slotStart.plusMinutes(SLOT_MINUTES).isBefore(dayEnd) || slotStart.plusMinutes(SLOT_MINUTES).equals(dayEnd)) {
            LocalDateTime slotEnd = slotStart.plusMinutes(SLOT_MINUTES);
            boolean available = !overlapsAny(slotStart, slotEnd, existing);
            slots.add(new TimeSlotDto(slotStart, slotEnd, available));
            slotStart = slotEnd;
        }
        return slots;
    }

    private static boolean overlapsAny(LocalDateTime start, LocalDateTime end, List<Appointment> appointments) {
        for (Appointment a : appointments) {
            if (a.getStatus() == AppointmentStatus.CANCELLED || a.getStatus() == AppointmentStatus.NO_SHOW) {
                continue;
            }
            if (intervalOverlaps(start, end, a.getStartTime(), a.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    private static boolean intervalOverlaps(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        return s1.isBefore(e2) && e1.isAfter(s2);
    }
}
