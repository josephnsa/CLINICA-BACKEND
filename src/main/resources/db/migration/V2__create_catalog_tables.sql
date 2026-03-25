-- ─── ESPECIALIDADES ──────────────────────────────────────────
CREATE TABLE specialties (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code       VARCHAR(20) UNIQUE NOT NULL,
    name       VARCHAR(150) NOT NULL,
    is_active  BOOLEAN     NOT NULL DEFAULT true
);

-- ─── SERVICIOS / PROCEDIMIENTOS ──────────────────────────────
CREATE TABLE services (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code         VARCHAR(30) UNIQUE NOT NULL,
    name         VARCHAR(200) NOT NULL,
    specialty_id UUID        REFERENCES specialties(id),
    duration_min INT         NOT NULL DEFAULT 30,
    price        NUMERIC(10,2) NOT NULL DEFAULT 0,
    is_active    BOOLEAN     NOT NULL DEFAULT true
);

-- ─── MÉDICOS / ESPECIALISTAS ──────────────────────────────────
CREATE TABLE doctors (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID        UNIQUE REFERENCES users(id),
    license_number VARCHAR(50) UNIQUE NOT NULL,
    specialty_id  UUID        REFERENCES specialties(id),
    is_active     BOOLEAN     NOT NULL DEFAULT true,
    created_at    TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ─── CIE-10 DIAGNÓSTICOS ──────────────────────────────────────
CREATE TABLE cie10_codes (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(10) UNIQUE NOT NULL,
    description VARCHAR(500) NOT NULL,
    category    VARCHAR(100)
);

-- ─── MEDICAMENTOS ─────────────────────────────────────────────
CREATE TABLE medications (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code           VARCHAR(30) UNIQUE NOT NULL,
    generic_name   VARCHAR(200) NOT NULL,
    commercial_name VARCHAR(200),
    presentation   VARCHAR(100),
    unit           VARCHAR(50),
    is_active      BOOLEAN     NOT NULL DEFAULT true
);

-- ─── TARIFARIOS ───────────────────────────────────────────────
CREATE TABLE tariffs (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    service_id UUID        REFERENCES services(id),
    sede_id    UUID        REFERENCES sedes(id),
    name       VARCHAR(100) NOT NULL,
    price      NUMERIC(10,2) NOT NULL,
    is_active  BOOLEAN     NOT NULL DEFAULT true,
    UNIQUE (service_id, sede_id)
);
