-- ─── PERMISOS DEL SISTEMA ────────────────────────────────────────────────────
-- Módulo: patients
INSERT INTO permissions (code, module, action, description) VALUES
    ('patients:read',  'patients', 'read',  'Ver pacientes y consentimientos'),
    ('patients:write', 'patients', 'write', 'Crear/editar pacientes y consentimientos');

-- Módulo: agenda
INSERT INTO permissions (code, module, action, description) VALUES
    ('agenda:read',         'agenda', 'read',         'Ver citas'),
    ('agenda:create',       'agenda', 'create',       'Crear citas'),
    ('agenda:cancel',       'agenda', 'cancel',       'Cancelar citas'),
    ('agenda:reschedule',   'agenda', 'reschedule',   'Reprogramar citas'),
    ('agenda:checkin',      'agenda', 'checkin',      'Check-in de citas'),
    ('agenda:start',        'agenda', 'start',        'Iniciar consulta'),
    ('agenda:complete',     'agenda', 'complete',     'Completar consulta'),
    ('agenda:noshow',       'agenda', 'noshow',       'Marcar no-show'),
    ('agenda:availability', 'agenda', 'availability', 'Gestionar disponibilidad'),
    ('agenda:triage',       'agenda', 'triage',       'Registrar triaje');

-- Módulo: catalog
INSERT INTO permissions (code, module, action, description) VALUES
    ('CATALOGO_READ',  'catalog', 'read',  'Ver catálogos (sedes, especialidades, servicios, médicos, medicamentos, CIE10)'),
    ('CATALOGO_WRITE', 'catalog', 'write', 'Crear/editar catálogos');

-- Módulo: billing
INSERT INTO permissions (code, module, action, description) VALUES
    ('FACTURACION_CREATE', 'billing', 'create', 'Crear facturas'),
    ('FACTURACION_READ',   'billing', 'read',   'Ver facturas'),
    ('FACTURACION_REFUND', 'billing', 'refund', 'Emitir reembolsos');

-- Módulo: clinical
INSERT INTO permissions (code, module, action, description) VALUES
    ('HISTORIA_CLINICA_READ',  'clinical', 'read',  'Ver notas clínicas'),
    ('HISTORIA_CLINICA_WRITE', 'clinical', 'write', 'Crear notas clínicas');

-- Módulo: exam
INSERT INTO permissions (code, module, action, description) VALUES
    ('EXAMENES_CREATE', 'exam', 'create', 'Crear órdenes de examen'),
    ('EXAMENES_READ',   'exam', 'read',   'Ver órdenes de examen'),
    ('EXAMENES_SIGN',   'exam', 'sign',   'Firmar órdenes de examen'),
    ('EXAMENES_RESULT', 'exam', 'result', 'Registrar resultados de examen');

-- Módulo: prescription
INSERT INTO permissions (code, module, action, description) VALUES
    ('PRESCRIPCIONES_CREATE',  'prescription', 'create',  'Crear prescripciones'),
    ('PRESCRIPCIONES_READ',    'prescription', 'read',    'Ver prescripciones'),
    ('PRESCRIPCIONES_DISPENSE','prescription', 'dispense','Dispensar prescripciones');

-- Módulo: inventory
INSERT INTO permissions (code, module, action, description) VALUES
    ('INVENTARIO_READ',  'inventory', 'read',  'Ver inventario, proveedores y órdenes de compra'),
    ('INVENTARIO_WRITE', 'inventory', 'write', 'Gestionar inventario, proveedores y órdenes de compra');

-- Módulo: hrm
INSERT INTO permissions (code, module, action, description) VALUES
    ('RR_HH_READ',  'hrm', 'read',  'Ver empleados'),
    ('RR_HH_WRITE', 'hrm', 'write', 'Gestionar empleados');

-- Módulo: reports / audit
INSERT INTO permissions (code, module, action, description) VALUES
    ('REPORTES_READ', 'reports', 'read', 'Ver reportes y auditoría');

-- Módulo: customer service
INSERT INTO permissions (code, module, action, description) VALUES
    ('ATENCION_CLIENTE_WRITE', 'customerservice', 'write', 'Gestionar quejas y atención al cliente');

-- ─── ASIGNACIÓN DE PERMISOS A ROLES ──────────────────────────────────────────

-- ADMIN: todos los permisos
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ADMIN';

-- MEDICO
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'MEDICO'
  AND p.code IN (
    'patients:read',
    'patients:write',
    'agenda:read',
    'agenda:start',
    'agenda:complete',
    'agenda:noshow',
    'agenda:triage',
    'HISTORIA_CLINICA_READ',
    'HISTORIA_CLINICA_WRITE',
    'EXAMENES_CREATE',
    'EXAMENES_READ',
    'EXAMENES_SIGN',
    'PRESCRIPCIONES_CREATE',
    'PRESCRIPCIONES_READ',
    'CATALOGO_READ'
  );

-- ENFERMERA
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ENFERMERA'
  AND p.code IN (
    'patients:read',
    'patients:write',
    'agenda:read',
    'agenda:checkin',
    'agenda:triage',
    'HISTORIA_CLINICA_READ',
    'EXAMENES_READ',
    'EXAMENES_RESULT',
    'CATALOGO_READ'
  );

-- RECEPCIONISTA
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'RECEPCIONISTA'
  AND p.code IN (
    'patients:read',
    'patients:write',
    'agenda:read',
    'agenda:create',
    'agenda:cancel',
    'agenda:reschedule',
    'agenda:checkin',
    'agenda:availability',
    'CATALOGO_READ',
    'ATENCION_CLIENTE_WRITE'
  );

-- CAJERO
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'CAJERO'
  AND p.code IN (
    'patients:read',
    'FACTURACION_CREATE',
    'FACTURACION_READ',
    'FACTURACION_REFUND',
    'CATALOGO_READ'
  );

-- FARMACEUTICO
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'FARMACEUTICO'
  AND p.code IN (
    'patients:read',
    'PRESCRIPCIONES_READ',
    'PRESCRIPCIONES_DISPENSE',
    'INVENTARIO_READ',
    'INVENTARIO_WRITE',
    'CATALOGO_READ'
  );

-- PACIENTE_PORTAL
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'PACIENTE_PORTAL'
  AND p.code IN (
    'patients:read',
    'agenda:read',
    'agenda:create',
    'agenda:cancel',
    'EXAMENES_READ',
    'PRESCRIPCIONES_READ'
  );
