-- PRESCRIPCIONES Y MEDICACIÓN

CREATE TABLE prescriptions (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id      UUID        NOT NULL REFERENCES patients(id),
    doctor_id       UUID        REFERENCES doctors(id),
    appointment_id  UUID        REFERENCES appointments(id),
    diagnosis_id    UUID        REFERENCES cie10_codes(id),
    notes           TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, CANCELLED, COMPLETED
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by      UUID        REFERENCES users(id)
);

CREATE TABLE prescription_items (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id  UUID        NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    medication_id    UUID        NOT NULL REFERENCES medications(id),
    dose             VARCHAR(100) NOT NULL,
    frequency        VARCHAR(100) NOT NULL,
    duration         VARCHAR(100),
    route            VARCHAR(50),
    instructions     VARCHAR(500),
    quantity         INT          NOT NULL DEFAULT 0,
    is_dispensed     BOOLEAN      NOT NULL DEFAULT false,
    dispensed_at     TIMESTAMP,
    dispensed_by     UUID         REFERENCES users(id)
);

CREATE INDEX idx_prescriptions_patient ON prescriptions(patient_id);
CREATE INDEX idx_prescriptions_appointment ON prescriptions(appointment_id);
CREATE INDEX idx_prescription_items_prescription ON prescription_items(prescription_id);

