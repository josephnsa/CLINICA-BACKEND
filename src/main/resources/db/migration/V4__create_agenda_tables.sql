-- ─── DISPONIBILIDAD DEL MÉDICO ────────────────────────────────
CREATE TABLE doctor_availability (
    id          UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    doctor_id   UUID    REFERENCES doctors(id),
    sede_id     UUID    REFERENCES sedes(id),
    day_of_week INT     NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time  TIME    NOT NULL,
    end_time    TIME    NOT NULL,
    is_active   BOOLEAN NOT NULL DEFAULT true
);

-- ─── BLOQUEOS (vacaciones, feriados) ─────────────────────────
CREATE TABLE availability_blocks (
    id          UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    doctor_id   UUID    REFERENCES doctors(id),
    sede_id     UUID    REFERENCES sedes(id),
    start_dt    TIMESTAMP NOT NULL,
    end_dt      TIMESTAMP NOT NULL,
    reason      VARCHAR(200)
);

-- ─── CITAS ────────────────────────────────────────────────────
CREATE TABLE appointments (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id      UUID        REFERENCES patients(id),
    doctor_id       UUID        REFERENCES doctors(id),
    service_id      UUID        REFERENCES services(id),
    sede_id         UUID        REFERENCES sedes(id),
    start_time      TIMESTAMP   NOT NULL,
    end_time        TIMESTAMP   NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING','CONFIRMED','CHECKED_IN',
                                      'IN_PROGRESS','ATTENDED','CANCELLED','NO_SHOW')),
    notes           TEXT,
    cancellation_reason TEXT,
    created_by      UUID        REFERENCES users(id),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ─── EVOLUCIONES CLÍNICAS ────────────────────────────────────
CREATE TABLE clinical_notes (
    id             UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_id UUID      REFERENCES appointments(id),
    patient_id     UUID      REFERENCES patients(id),
    doctor_id      UUID      REFERENCES doctors(id),
    reason         TEXT,
    physical_exam  TEXT,
    diagnosis_code VARCHAR(10) REFERENCES cie10_codes(code),
    diagnosis_desc TEXT,
    treatment_plan TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ─── ÍNDICES para performance ────────────────────────────────
CREATE INDEX idx_appointments_doctor_date ON appointments(doctor_id, start_time);
CREATE INDEX idx_appointments_patient     ON appointments(patient_id);
CREATE INDEX idx_appointments_sede_date   ON appointments(sede_id, start_time);
CREATE INDEX idx_appointments_status      ON appointments(status);
