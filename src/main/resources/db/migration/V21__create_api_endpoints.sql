-- V21: Tabla de endpoints de la API con permisos requeridos por rol
-- Permite administrar qué permiso necesita cada endpoint y auditar el acceso por módulo

CREATE TABLE IF NOT EXISTS api_endpoints (
    id              UUID         DEFAULT gen_random_uuid() PRIMARY KEY,
    path            VARCHAR(500) NOT NULL,
    http_method     VARCHAR(10)  NOT NULL,
    description     VARCHAR(500),
    permission_code VARCHAR(100),
    module          VARCHAR(100),
    is_public       BOOLEAN      DEFAULT FALSE,
    is_active       BOOLEAN      DEFAULT TRUE,
    CONSTRAINT uq_api_endpoint UNIQUE (path, http_method)
);

COMMENT ON TABLE api_endpoints IS 'Catálogo de endpoints REST con el permiso requerido para acceder a cada uno';

-- ─── AUTH (públicos) ──────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/auth/login',           'POST', 'Inicio de sesión con email/password',  NULL, 'AUTH', TRUE),
('/api/auth/google/login',    'POST', 'Inicio de sesión con Google OAuth',    NULL, 'AUTH', TRUE),
('/api/auth/refresh',         'POST', 'Renovar token de acceso',              NULL, 'AUTH', TRUE),
('/api/auth/me',              'GET',  'Obtener usuario autenticado',           NULL, 'AUTH', FALSE),
('/api/auth/menu',            'GET',  'Obtener menú del usuario',             NULL, 'AUTH', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── DASHBOARD ────────────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/dashboard/summary',    'GET', 'Resumen del dashboard',          'REPORTES_READ', 'REPORTES',   FALSE),
('/api/dashboard/revenue',    'GET', 'Ingresos en el dashboard',       'REPORTES_READ', 'REPORTES',   FALSE),
('/api/dashboard/me',         'GET', 'Info del usuario en dashboard',  NULL,            'DASHBOARD',  FALSE),
('/api/dashboard/sede-info',  'GET', 'Info de la sede actual',         NULL,            'DASHBOARD',  FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── AGENDA / CITAS ──────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/appointments',                          'POST',   'Crear cita',              'agenda:create',       'AGENDA', FALSE),
('/api/appointments',                          'GET',    'Listar citas',            'agenda:read',         'AGENDA', FALSE),
('/api/appointments/availability',             'GET',    'Ver disponibilidad',      'agenda:availability', 'AGENDA', FALSE),
('/api/appointments/{id}',                     'DELETE', 'Cancelar cita',           'agenda:cancel',       'AGENDA', FALSE),
('/api/appointments/{id}/reschedule',          'PATCH',  'Reprogramar cita',        'agenda:reschedule',   'AGENDA', FALSE),
('/api/appointments/{id}/confirm',             'PATCH',  'Confirmar cita',          'agenda:confirm',      'AGENDA', FALSE),
('/api/appointments/{id}/checkin',             'PATCH',  'Check-in de paciente',    'agenda:checkin',      'AGENDA', FALSE),
('/api/appointments/{id}/start-consultation',  'PATCH',  'Iniciar consulta',        'agenda:start',        'AGENDA', FALSE),
('/api/appointments/{id}/complete',            'PATCH',  'Completar consulta',      'agenda:complete',     'AGENDA', FALSE),
('/api/appointments/{id}/no-show',             'PATCH',  'Registrar no-show',       'agenda:noshow',       'AGENDA', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── PACIENTES ────────────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/patients',      'POST', 'Registrar paciente',   'patients:write', 'PACIENTES', FALSE),
('/api/patients',      'GET',  'Listar pacientes',      'patients:read',  'PACIENTES', FALSE),
('/api/patients/{id}', 'GET',  'Ver ficha de paciente', 'patients:read',  'PACIENTES', FALSE),
('/api/patients/{id}', 'PUT',  'Actualizar paciente',   'patients:write', 'PACIENTES', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── FACTURACIÓN ─────────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/invoices',                       'POST',  'Crear comprobante de pago',   'FACTURACION_CREATE', 'FACTURACION', FALSE),
('/api/invoices',                       'GET',   'Listar comprobantes',          'FACTURACION_READ',   'FACTURACION', FALSE),
('/api/invoices/{id}',                  'GET',   'Ver comprobante',              'FACTURACION_READ',   'FACTURACION', FALSE),
('/api/invoices/cash-register-summary', 'GET',   'Resumen / cierre de caja',    'FACTURACION_READ',   'FACTURACION', FALSE),
('/api/invoices/{id}/pdf',              'GET',   'Descargar PDF del comprobante','FACTURACION_READ',   'FACTURACION', FALSE),
('/api/invoices/{id}/refund',           'PATCH', 'Emitir nota de crédito',       'FACTURACION_REFUND', 'FACTURACION', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── HISTORIA CLÍNICA ─────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/clinical-notes', 'POST', 'Crear nota clínica',    'HISTORIA_CLINICA_WRITE', 'HISTORIA_CLINICA', FALSE),
('/api/clinical-notes', 'GET',  'Ver historia clínica',  'HISTORIA_CLINICA_READ',  'HISTORIA_CLINICA', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── INVENTARIO / FARMACIA ────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/inventory',      'POST', 'Registrar ítem en inventario', 'INVENTARIO_WRITE', 'INVENTARIO', FALSE),
('/api/inventory',      'GET',  'Consultar inventario',          'INVENTARIO_READ',  'INVENTARIO', FALSE),
('/api/inventory/{id}', 'PUT',  'Actualizar ítem',               'INVENTARIO_WRITE', 'INVENTARIO', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── PRESCRIPCIONES ──────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/prescriptions',              'POST',  'Crear prescripción',       'PRESCRIPCIONES_CREATE',   'PRESCRIPCIONES', FALSE),
('/api/prescriptions',              'GET',   'Listar prescripciones',     'PRESCRIPCIONES_READ',     'PRESCRIPCIONES', FALSE),
('/api/prescriptions/{id}/dispense','PATCH', 'Dispensar prescripción',   'PRESCRIPCIONES_DISPENSE', 'PRESCRIPCIONES', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── EXÁMENES ────────────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/exam-orders',            'POST',  'Crear orden de examen',         'EXAMENES_CREATE', 'EXAMENES', FALSE),
('/api/exam-orders',            'GET',   'Listar órdenes de examen',      'EXAMENES_READ',   'EXAMENES', FALSE),
('/api/exam-orders/{id}/sign',  'PATCH', 'Firmar resultado de examen',    'EXAMENES_SIGN',   'EXAMENES', FALSE),
('/api/exam-orders/{id}/result','PATCH', 'Registrar resultado de examen', 'EXAMENES_RESULT', 'EXAMENES', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── RR.HH. ───────────────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/employees',      'POST',   'Registrar empleado',   'RR_HH_WRITE', 'RRHH', FALSE),
('/api/employees',      'GET',    'Listar empleados',      'RR_HH_READ',  'RRHH', FALSE),
('/api/employees/{id}', 'GET',    'Ver empleado',          'RR_HH_READ',  'RRHH', FALSE),
('/api/employees/{id}', 'PUT',    'Actualizar empleado',   'RR_HH_WRITE', 'RRHH', FALSE),
('/api/employees/{id}', 'DELETE', 'Eliminar empleado',     'RR_HH_WRITE', 'RRHH', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── REPORTES ────────────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/reports/operativos',  'GET', 'Reportes operativos',       'REPORTES_READ', 'REPORTES', FALSE),
('/api/reports/clinicos',    'GET', 'Reportes clínicos',         'REPORTES_READ', 'REPORTES', FALSE),
('/api/reports/financieros', 'GET', 'Reportes financieros',      'REPORTES_READ', 'REPORTES', FALSE),
('/api/reports/inventario',  'GET', 'Reportes de inventario',    'REPORTES_READ', 'REPORTES', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── SEGURIDAD / USUARIOS ────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/security/users',        'GET',   'Listar usuarios del sistema',   'CATALOGO_READ',  'SEGURIDAD', FALSE),
('/api/security/users',        'POST',  'Crear usuario del sistema',     'CATALOGO_WRITE', 'SEGURIDAD', FALSE),
('/api/security/users/{id}',   'PUT',   'Actualizar usuario',            'CATALOGO_WRITE', 'SEGURIDAD', FALSE),
('/api/security/users/{id}',   'PATCH', 'Cambiar estado de usuario',     'CATALOGO_WRITE', 'SEGURIDAD', FALSE),
('/api/security/roles',        'GET',   'Listar roles',                  'CATALOGO_READ',  'SEGURIDAD', FALSE),
('/api/security/audit-logs',   'GET',   'Ver auditoría del sistema',     'REPORTES_READ',  'SEGURIDAD', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── CATÁLOGO CLÍNICO ────────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/catalog/services',     'GET',  'Ver servicios clínicos',      'CATALOGO_READ',  'CATALOGO', FALSE),
('/api/catalog/services',     'POST', 'Crear servicio clínico',      'CATALOGO_WRITE', 'CATALOGO', FALSE),
('/api/catalog/services/{id}','PUT',  'Actualizar servicio',          'CATALOGO_WRITE', 'CATALOGO', FALSE),
('/api/catalog/doctors',      'GET',  'Ver especialistas',            'CATALOGO_READ',  'CATALOGO', FALSE),
('/api/catalog/doctors',      'POST', 'Registrar especialista',       'CATALOGO_WRITE', 'CATALOGO', FALSE),
('/api/catalog/cie10',        'GET',  'Consultar CIE-10',             'CATALOGO_READ',  'CATALOGO', FALSE),
('/api/catalog/medications',  'GET',  'Ver medicamentos',             'CATALOGO_READ',  'CATALOGO', FALSE),
('/api/catalog/medications',  'POST', 'Registrar medicamento',        'CATALOGO_WRITE', 'CATALOGO', FALSE),
('/api/catalog/tariffs',      'GET',  'Ver tarifarios/convenios',    'CATALOGO_READ',  'CATALOGO', FALSE),
('/api/catalog/tariffs',      'POST', 'Crear tarifario',              'CATALOGO_WRITE', 'CATALOGO', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── ATENCIÓN AL CLIENTE ─────────────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/complaints',      'POST',  'Registrar reclamo',        'ATENCION_CLIENTE_WRITE', 'ATENCION_CLIENTE', FALSE),
('/api/complaints',      'GET',   'Listar reclamos',           'ATENCION_CLIENTE_READ',  'ATENCION_CLIENTE', FALSE),
('/api/complaints/{id}', 'PATCH', 'Actualizar estado reclamo', 'ATENCION_CLIENTE_WRITE', 'ATENCION_CLIENTE', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;

-- ─── API ENDPOINTS (auto-gestión) ────────────────────────────────────────────
INSERT INTO api_endpoints (path, http_method, description, permission_code, module, is_public) VALUES
('/api/security/api-endpoints',      'GET',   'Listar endpoints del sistema',  'CATALOGO_READ',  'SEGURIDAD', FALSE),
('/api/security/api-endpoints',      'POST',  'Registrar nuevo endpoint',       'CATALOGO_WRITE', 'SEGURIDAD', FALSE),
('/api/security/api-endpoints/{id}', 'PATCH', 'Actualizar endpoint',            'CATALOGO_WRITE', 'SEGURIDAD', FALSE)
ON CONFLICT (path, http_method) DO NOTHING;
