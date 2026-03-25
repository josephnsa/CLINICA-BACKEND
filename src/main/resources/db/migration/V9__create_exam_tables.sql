-- EXÁMENES Y RESULTADOS

CREATE TABLE exam_orders (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id     UUID        NOT NULL REFERENCES patients(id),
    doctor_id      UUID        REFERENCES doctors(id),
    appointment_id UUID        REFERENCES appointments(id),
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    notes          TEXT,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by     UUID        REFERENCES users(id)
);

CREATE TABLE exam_order_items (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id     UUID        NOT NULL REFERENCES exam_orders(id) ON DELETE CASCADE,
    service_id   UUID        NOT NULL REFERENCES services(id),
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    result_text  TEXT,
    result_at    TIMESTAMP,
    result_by    UUID        REFERENCES users(id)
);

CREATE INDEX idx_exam_orders_patient ON exam_orders(patient_id);
CREATE INDEX idx_exam_orders_appointment ON exam_orders(appointment_id);
CREATE INDEX idx_exam_items_order ON exam_order_items(order_id);

