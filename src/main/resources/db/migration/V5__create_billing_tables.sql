-- FACTURACION Y CAJA
CREATE TABLE invoices (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    serie          VARCHAR(10) NOT NULL,
    number         INT         NOT NULL,
    patient_id      UUID        REFERENCES patients(id),
    sede_id         UUID        REFERENCES sedes(id),
    appointment_id  UUID        REFERENCES appointments(id),
    subtotal        NUMERIC(10,2) NOT NULL DEFAULT 0,
    tax             NUMERIC(10,2) NOT NULL DEFAULT 0,
    total           NUMERIC(10,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING','PAID','CANCELLED','REFUNDED')),
    notes           TEXT,
    created_by      UUID        REFERENCES users(id),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (serie, number)
);

CREATE TABLE invoice_items (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id  UUID        REFERENCES invoices(id) ON DELETE CASCADE,
    service_id  UUID        REFERENCES services(id),
    description VARCHAR(300) NOT NULL,
    quantity    INT         NOT NULL DEFAULT 1,
    unit_price  NUMERIC(10,2) NOT NULL,
    total       NUMERIC(10,2) NOT NULL
);

CREATE TABLE payments (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id   UUID        REFERENCES invoices(id),
    method       VARCHAR(20) NOT NULL
                 CHECK (method IN ('CASH','CARD','TRANSFER','INSURANCE')),
    amount       NUMERIC(10,2) NOT NULL,
    reference    VARCHAR(100),
    paid_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by   UUID        REFERENCES users(id)
);

CREATE INDEX idx_invoices_sede_created ON invoices(sede_id, created_at);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
