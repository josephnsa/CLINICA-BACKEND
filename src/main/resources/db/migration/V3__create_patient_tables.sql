-- ─── PACIENTES ────────────────────────────────────────────────
CREATE TABLE patients (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    doc_type        VARCHAR(10)  NOT NULL DEFAULT 'DNI',
    doc_number      VARCHAR(20)  UNIQUE NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    birth_date      DATE,
    gender          VARCHAR(10)  CHECK (gender IN ('M','F','OTHER')),
    email           VARCHAR(200),
    phone           VARCHAR(20),
    address         VARCHAR(300),
    blood_type      VARCHAR(5),
    emergency_name  VARCHAR(200),
    emergency_phone VARCHAR(20),
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ─── FICHA CLÍNICA BASE (alergias, antecedentes) ──────────────
CREATE TABLE patient_clinical_profiles (
    id               UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id       UUID    UNIQUE REFERENCES patients(id),
    allergies        TEXT,
    personal_history TEXT,
    family_history   TEXT,
    surgical_history TEXT,
    current_meds     TEXT,
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ─── CONSENTIMIENTOS ──────────────────────────────────────────
CREATE TABLE patient_consents (
    id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id  UUID      REFERENCES patients(id),
    type        VARCHAR(50) NOT NULL,
    file_url    VARCHAR(500),
    signed_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  UUID      REFERENCES users(id)
);
