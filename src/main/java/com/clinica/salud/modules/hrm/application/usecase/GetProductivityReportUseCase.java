package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.application.dto.ProductivityReport;
import com.clinica.salud.modules.hrm.domain.model.AttendanceRecord;
import com.clinica.salud.modules.hrm.domain.model.AttendanceStatus;
import com.clinica.salud.modules.hrm.domain.model.Employee;
import com.clinica.salud.modules.hrm.domain.port.AttendanceRepository;
import com.clinica.salud.modules.hrm.domain.port.EmployeeRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProductivityReportUseCase {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public ProductivityReport execute(UUID employeeId, LocalDate from, LocalDate to) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId.toString()));

        List<AttendanceRecord> records = attendanceRepository
                .findByEmployeeIdAndDateBetween(employeeId, from, to);

        long totalDays    = records.size();
        long presentDays  = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
        long absentDays   = records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();
        long lateDays     = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
        long excusedDays  = records.stream().filter(r -> r.getStatus() == AttendanceStatus.EXCUSED).count();
        int  totalMinutes = records.stream()
                .mapToInt(r -> r.getMinutesWorked() != null ? r.getMinutesWorked() : 0)
                .sum();

        double attendanceRate = totalDays > 0
                ? Math.round(((double)(presentDays + lateDays) / totalDays) * 10000.0) / 100.0
                : 0.0;
        double avgMinutes = (presentDays + lateDays) > 0
                ? (double) totalMinutes / (presentDays + lateDays)
                : 0.0;

        // Productividad clínica: citas atendidas vía user_id del empleado (si es médico)
        int appointmentsAttended = 0;
        double avgAppointmentDuration = 0.0;

        if (employee.getUserId() != null) {
            String sql = """
                    SELECT COUNT(*) AS total,
                           COALESCE(AVG(EXTRACT(EPOCH FROM (end_time - start_time)) / 60), 0) AS avg_min
                    FROM appointments
                    WHERE doctor_id = (SELECT id FROM doctors WHERE user_id = ?)
                      AND status = 'ATTENDED'
                      AND start_time >= ? AND start_time <= ?
                    """;
            var result = jdbcTemplate.queryForMap(sql,
                    employee.getUserId(), from.atStartOfDay(), to.plusDays(1).atStartOfDay());

            appointmentsAttended = ((Number) result.get("total")).intValue();
            avgAppointmentDuration = ((Number) result.get("avg_min")).doubleValue();
        }

        return ProductivityReport.builder()
                .employeeId(employeeId)
                .employeeFullName(employee.getFullName())
                .position(employee.getPosition())
                .from(from)
                .to(to)
                .totalDays((int) totalDays)
                .presentDays((int) presentDays)
                .absentDays((int) absentDays)
                .lateDays((int) lateDays)
                .excusedDays((int) excusedDays)
                .attendanceRate(attendanceRate)
                .totalMinutesWorked(totalMinutes)
                .averageMinutesPerDay(Math.round(avgMinutes * 100.0) / 100.0)
                .appointmentsAttended(appointmentsAttended)
                .averageAppointmentDurationMinutes(Math.round(avgAppointmentDuration * 100.0) / 100.0)
                .build();
    }
}
