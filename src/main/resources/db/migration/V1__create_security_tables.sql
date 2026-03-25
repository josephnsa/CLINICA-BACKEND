-- Extensión para UUIDs
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ─── SEDES (multi-sede) ──────────────────────────────────────────
CREATE TABLE sedes (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(20) UNIQUE NOT NULL,
    name        VARCHAR(150) NOT NULL,
    address     VARCHAR(300),
    phone       VARCHAR(20),
    email       VARCHAR(200),
    is_active   BOOLEAN     NOT NULL DEFAULT true,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ─── ROLES ────────────────────────────────────────────────────────
CREATE TABLE roles (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(50) UNIQUE NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(300),
    is_active   BOOLEAN     NOT NULL DEFAULT true,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ─── PERMISOS ─────────────────────────────────────────────────────
CREATE TABLE permissions (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(100) UNIQUE NOT NULL,
    module      VARCHAR(50) NOT NULL,
    action      VARCHAR(30) NOT NULL,
    description VARCHAR(200)
);

-- ─── ROL <-> PERMISOS ─────────────────────────────────────────────
CREATE TABLE role_permissions (
    role_id       UUID REFERENCES roles(id)       ON DELETE CASCADE,
    permission_id UUID REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- ─── USUARIOS ─────────────────────────────────────────────────────
CREATE TABLE users (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email        VARCHAR(200) UNIQUE NOT NULL,
    password     VARCHAR(255) NOT NULL,
    full_name    VARCHAR(200) NOT NULL,
    role_id      UUID        REFERENCES roles(id),
    is_active    BOOLEAN     NOT NULL DEFAULT true,
    last_login   TIMESTAMP,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ─── USUARIO <-> SEDES ────────────────────────────────────────────
CREATE TABLE user_sedes (
    user_id  UUID REFERENCES users(id) ON DELETE CASCADE,
    sede_id  UUID REFERENCES sedes(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, sede_id)
);

-- ─── MENÚ ─────────────────────────────────────────────────────────
CREATE TABLE menu_items (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_id   UUID        REFERENCES menu_items(id),
    label       VARCHAR(100) NOT NULL,
    icon        VARCHAR(80),
    route       VARCHAR(200),
    order_index INT         NOT NULL DEFAULT 0,
    is_active   BOOLEAN     NOT NULL DEFAULT true
);

-- ─── ROL <-> MENÚ ─────────────────────────────────────────────────
CREATE TABLE role_menu_items (
    role_id      UUID REFERENCES roles(id)      ON DELETE CASCADE,
    menu_item_id UUID REFERENCES menu_items(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, menu_item_id)
);

-- ─── AUDITORÍA ────────────────────────────────────────────────────
CREATE TABLE audit_logs (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        REFERENCES users(id),
    action     VARCHAR(100) NOT NULL,
    module     VARCHAR(50) NOT NULL,
    entity_id  VARCHAR(100),
    details    TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ─── DATOS INICIALES (seed) ───────────────────────────────────────
INSERT INTO sedes (code, name, address) VALUES ('SEDE01', 'Sede Principal', 'Av. Principal 123');

INSERT INTO roles (code, name) VALUES
    ('ADMIN',         'Administrador'),
    ('MEDICO',        'Médico'),
    ('ENFERMERA',     'Enfermera'),
    ('RECEPCIONISTA', 'Recepcionista'),
    ('CAJERO',        'Cajero'),
    ('FARMACEUTICO',  'Farmacéutico');

-- Usuario admin inicial (password: Admin2025! — cambiar en producción)
-- Hash bcrypt de 'Admin2025!':
INSERT INTO users (email, password, full_name, role_id)
VALUES ('admin@clinica.pe',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQyCgN8GXlt.wHLnNsV4f2g5K',
        'Administrador Sistema',
        (SELECT id FROM roles WHERE code = 'ADMIN'));
