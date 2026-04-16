-- ─── PORTAL DEL PACIENTE ─────────────────────────────────────────────────────
-- Tabla de cuentas para acceso de pacientes al portal web/móvil.
-- Separada de la tabla users (que es exclusiva para personal de la clínica).
CREATE TABLE patient_accounts (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID         NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    is_active  BOOLEAN      NOT NULL DEFAULT true,
    last_login TIMESTAMP,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_patient_accounts_patient_id ON patient_accounts(patient_id);
CREATE INDEX idx_patient_accounts_email      ON patient_accounts(email);

-- ─── NUEVO PERMISO: ATENCION_CLIENTE_READ ────────────────────────────────────
INSERT INTO permissions (code, module, action, description) VALUES
    ('ATENCION_CLIENTE_READ', 'customerservice', 'read', 'Ver reclamos y encuestas de satisfacción')
ON CONFLICT (code) DO NOTHING;

-- Asignar a ADMIN (ya tiene todos, pero por coherencia se incluye)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ADMIN'
  AND p.code = 'ATENCION_CLIENTE_READ'
ON CONFLICT DO NOTHING;

-- Asignar a RECEPCIONISTA
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'RECEPCIONISTA'
  AND p.code = 'ATENCION_CLIENTE_READ'
ON CONFLICT DO NOTHING;
