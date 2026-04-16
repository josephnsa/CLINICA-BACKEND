-- ─── CONTROL DE ASISTENCIA DE EMPLEADOS ─────────────────────────────────────
CREATE TABLE employee_attendance (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id    UUID         NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    sede_id        UUID         NOT NULL REFERENCES sedes(id),
    date           DATE         NOT NULL,
    check_in       TIMESTAMP,
    check_out      TIMESTAMP,
    minutes_worked INTEGER,
    status         VARCHAR(20)  NOT NULL
                                CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED')),
    notes          VARCHAR(500),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_attendance_employee_date UNIQUE (employee_id, date)
);

CREATE INDEX idx_attendance_employee_id   ON employee_attendance(employee_id);
CREATE INDEX idx_attendance_sede_date     ON employee_attendance(sede_id, date);
CREATE INDEX idx_attendance_date          ON employee_attendance(date);
