-- V10: Agregar estado DRAFT a facturas (proformas/presupuestos)
-- y mejoras de lógica de negocio al esquema

-- 1. Actualizar CHECK constraint de invoices para incluir DRAFT
ALTER TABLE invoices DROP CONSTRAINT IF EXISTS invoices_status_check;
ALTER TABLE invoices ADD CONSTRAINT invoices_status_check
    CHECK (status IN ('DRAFT','PENDING','PAID','CANCELLED','REFUNDED'));

-- 2. Tabla de lotes de inventario (fechas de vencimiento por lote)
CREATE TABLE inventory_batches (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id         UUID        NOT NULL REFERENCES inventory_items(id),
    batch_number    VARCHAR(50) NOT NULL,
    quantity        INT         NOT NULL DEFAULT 0,
    expiry_date     DATE        NOT NULL,
    received_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    notes           TEXT,
    CONSTRAINT chk_batch_quantity CHECK (quantity >= 0)
);

CREATE INDEX idx_inventory_batches_item   ON inventory_batches(item_id);
CREATE INDEX idx_inventory_batches_expiry ON inventory_batches(expiry_date);

-- 3. Tabla de proveedores (para compras)
CREATE TABLE suppliers (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(200) NOT NULL,
    ruc         VARCHAR(20),
    contact     VARCHAR(100),
    phone       VARCHAR(30),
    email       VARCHAR(150),
    address     TEXT,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- 4. Órdenes de compra a proveedores
CREATE TABLE purchase_orders (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id  UUID        NOT NULL REFERENCES suppliers(id),
    sede_id      UUID        NOT NULL REFERENCES sedes(id),
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                 CHECK (status IN ('PENDING','APPROVED','RECEIVED','CANCELLED')),
    total        NUMERIC(10,2) NOT NULL DEFAULT 0,
    notes        TEXT,
    created_by   UUID        REFERENCES users(id),
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    received_at  TIMESTAMP
);

CREATE TABLE purchase_order_items (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id   UUID        NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    medication_id       UUID        REFERENCES medications(id),
    description         VARCHAR(300) NOT NULL,
    quantity            INT         NOT NULL,
    unit_price          NUMERIC(10,2) NOT NULL,
    total               NUMERIC(10,2) NOT NULL
);

CREATE INDEX idx_purchase_orders_supplier ON purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_sede     ON purchase_orders(sede_id);

-- 5. Kardex de medicación por paciente
CREATE TABLE medication_kardex (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id      UUID        NOT NULL REFERENCES patients(id),
    prescription_id UUID        REFERENCES prescriptions(id),
    medication_id   UUID        NOT NULL REFERENCES medications(id),
    action          VARCHAR(20) NOT NULL
                    CHECK (action IN ('PRESCRIBED','DISPENSED','SUSPENDED','COMPLETED')),
    quantity        INT,
    notes           TEXT,
    recorded_by     UUID        REFERENCES users(id),
    recorded_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_kardex_patient    ON medication_kardex(patient_id);
CREATE INDEX idx_kardex_medication ON medication_kardex(medication_id);

-- 6. Firma profesional en órdenes de examen
ALTER TABLE exam_orders ADD COLUMN IF NOT EXISTS
    signed_by   UUID REFERENCES users(id);
ALTER TABLE exam_orders ADD COLUMN IF NOT EXISTS
    signed_at   TIMESTAMP;

-- 7. Consentimientos informados de pacientes
CREATE TABLE patient_consents (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id  UUID        NOT NULL REFERENCES patients(id),
    type        VARCHAR(50) NOT NULL,   -- GENERAL, SURGERY, ANESTHESIA, DATA_PRIVACY
    signed_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    signed_by   VARCHAR(200),           -- nombre del firmante (puede ser tutor si es menor)
    document_url TEXT,
    notes       TEXT
);

CREATE INDEX idx_consents_patient ON patient_consents(patient_id);

-- 8. Reclamos e incidencias (Atención al Cliente)
CREATE TABLE complaints (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id      UUID        REFERENCES patients(id),
    sede_id         UUID        REFERENCES sedes(id),
    type            VARCHAR(30) NOT NULL
                    CHECK (type IN ('COMPLAINT','INCIDENT','SUGGESTION','COMPLIMENT')),
    subject         VARCHAR(200) NOT NULL,
    description     TEXT        NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'OPEN'
                    CHECK (status IN ('OPEN','IN_PROGRESS','RESOLVED','CLOSED')),
    priority        VARCHAR(10) NOT NULL DEFAULT 'MEDIUM'
                    CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    assigned_to     UUID        REFERENCES users(id),
    resolved_at     TIMESTAMP,
    resolution      TEXT,
    created_by      UUID        REFERENCES users(id),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_complaints_patient ON complaints(patient_id);
CREATE INDEX idx_complaints_status  ON complaints(status);

-- 9. Encuestas de satisfacción
CREATE TABLE satisfaction_surveys (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id      UUID        NOT NULL REFERENCES patients(id),
    appointment_id  UUID        REFERENCES appointments(id),
    score           INT         NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment         TEXT,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_surveys_patient ON satisfaction_surveys(patient_id);

-- 10. Tabla HRM: empleados/profesionales de salud
CREATE TABLE employees (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        REFERENCES users(id),
    sede_id         UUID        REFERENCES sedes(id),
    specialty_id    UUID        REFERENCES specialties(id),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    doc_type        VARCHAR(10),
    doc_number      VARCHAR(20),
    license_number  VARCHAR(50),          -- colegiatura
    position        VARCHAR(100),         -- cargo
    hire_date       DATE,
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    phone           VARCHAR(30),
    email           VARCHAR(150),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE employee_schedules (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID        NOT NULL REFERENCES employees(id),
    day_of_week     INT         NOT NULL CHECK (day_of_week BETWEEN 0 AND 6), -- 0=Lunes
    start_time      TIME        NOT NULL,
    end_time        TIME        NOT NULL,
    sede_id         UUID        REFERENCES sedes(id),
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_employees_sede    ON employees(sede_id);
CREATE INDEX idx_employees_user    ON employees(user_id);
CREATE INDEX idx_schedules_employee ON employee_schedules(employee_id);
