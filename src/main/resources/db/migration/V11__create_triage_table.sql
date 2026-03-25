-- V11: Tabla de triaje para registro de signos vitales pre-consulta

CREATE TABLE triage (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_id  UUID        NOT NULL REFERENCES appointments(id),
    patient_id      UUID        NOT NULL REFERENCES patients(id),
    recorded_by     UUID        REFERENCES users(id),

    -- Signos vitales
    blood_pressure  VARCHAR(20),        -- ej. "120/80"
    heart_rate      INT,               -- lpm
    respiratory_rate INT,              -- rpm
    temperature     NUMERIC(4,1),      -- °C
    oxygen_saturation INT,             -- %
    weight          NUMERIC(5,2),      -- kg
    height          NUMERIC(5,2),      -- cm

    -- Clasificación y observaciones
    triage_level    VARCHAR(20) NOT NULL DEFAULT 'NORMAL'
                    CHECK (triage_level IN ('EMERGENCY','URGENT','NORMAL','LOW')),
    notes           TEXT,
    recorded_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_triage_appointment ON triage(appointment_id);
CREATE INDEX idx_triage_patient     ON triage(patient_id);
