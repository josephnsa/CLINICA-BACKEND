-- Seed de menú inicial y asociación con rol ADMIN

-- Top-level: Seguridad y Accesos
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('11111111-1111-1111-1111-111111111111', NULL, 'Seguridad y Accesos', 'pi pi-shield', NULL, 10, true),
    ('11111111-1111-1111-1111-111111111112', '11111111-1111-1111-1111-111111111111', 'Usuarios / Roles / Permisos', 'pi pi-users', '/seguridad/usuarios', 11, true),
    ('11111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111111', 'Autenticación', 'pi pi-lock', '/seguridad/autenticacion', 12, true),
    ('11111111-1111-1111-1111-111111111114', '11111111-1111-1111-1111-111111111111', 'Auditoría de acciones', 'pi pi-history', '/seguridad/auditoria', 13, true),
    ('11111111-1111-1111-1111-111111111115', '11111111-1111-1111-1111-111111111111', 'Parámetros de sede (multisede)', 'pi pi-building', '/seguridad/parametros-sede', 14, true);

-- Maestro Clínico
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('22222222-2222-2222-2222-222222222221', NULL, 'Maestro Clínico', 'pi pi-book', NULL, 20, true),
    ('22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222221', 'Catálogo de servicios', 'pi pi-list', '/maestro/servicios', 21, true),
    ('22222222-2222-2222-2222-222222222223', '22222222-2222-2222-2222-222222222221', 'Especialistas / especialidades', 'pi pi-id-card', '/maestro/especialistas', 22, true),
    ('22222222-2222-2222-2222-222222222224', '22222222-2222-2222-2222-222222222221', 'Catálogo CIE-10 / diagnósticos', 'pi pi-heart', '/maestro/cie10', 23, true),
    ('22222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222221', 'Medicamentos / presentaciones', 'pi pi-briefcase', '/maestro/medicamentos', 24, true),
    ('22222222-2222-2222-2222-222222222226', '22222222-2222-2222-2222-222222222221', 'Tarifarios / convenios / seguros', 'pi pi-credit-card', '/maestro/tarifarios', 25, true);

-- Pacientes e Historia Clínica
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('33333333-3333-3333-3333-333333333331', NULL, 'Pacientes e Historia Clínica', 'pi pi-user', NULL, 30, true),
    ('33333333-3333-3333-3333-333333333332', '33333333-3333-3333-3333-333333333331', 'Registro de paciente', 'pi pi-user-plus', '/pacientes/registro', 31, true),
    ('33333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333331', 'Consentimientos y documentos', 'pi pi-file-edit', '/pacientes/consentimientos', 32, true),
    ('33333333-3333-3333-3333-333333333334', '33333333-3333-3333-3333-333333333331', 'Ficha clínica básica', 'pi pi-notebook', '/pacientes/ficha-clinica', 33, true),
    ('33333333-3333-3333-3333-333333333335', '33333333-3333-3333-3333-333333333331', 'Evoluciones / notas clínicas', 'pi pi-file', '/pacientes/evoluciones', 34, true);

-- Agenda, Disponibilidad y Atención
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('44444444-4444-4444-4444-444444444441', NULL, 'Agenda, Disponibilidad y Atención', 'pi pi-calendar', NULL, 40, true),
    ('44444444-4444-4444-4444-444444444442', '44444444-4444-4444-4444-444444444441', 'Calendarios por especialista', 'pi pi-calendar-plus', '/agenda/calendarios', 41, true),
    ('44444444-4444-4444-4444-444444444443', '44444444-4444-4444-4444-444444444441', 'Reglas de disponibilidad', 'pi pi-sliders-h', '/agenda/disponibilidad', 42, true),
    ('44444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444441', 'Citas', 'pi pi-calendar-times', '/agenda/citas', 43, true),
    ('44444444-4444-4444-4444-444444444445', '44444444-4444-4444-4444-444444444441', 'Admisión / check-in', 'pi pi-sign-in', '/agenda/admision', 44, true),
    ('44444444-4444-4444-4444-444444444446', '44444444-4444-4444-4444-444444444441', 'Triaje', 'pi pi-heartbeat', '/agenda/triaje', 45, true),
    ('44444444-4444-4444-4444-444444444447', '44444444-4444-4444-4444-444444444441', 'Consulta', 'pi pi-user-md', '/agenda/consulta', 46, true);

-- Portal del Paciente
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('55555555-5555-5555-5555-555555555551', NULL, 'Portal del Paciente', 'pi pi-globe', NULL, 50, true),
    ('55555555-5555-5555-5555-555555555552', '55555555-5555-5555-5555-555555555551', 'Registro / inicio de sesión', 'pi pi-user', '/portal/registro', 51, true),
    ('55555555-5555-5555-5555-555555555553', '55555555-5555-5555-5555-555555555551', 'Búsqueda de especialista / servicio', 'pi pi-search', '/portal/busqueda', 52, true),
    ('55555555-5555-5555-5555-555555555554', '55555555-5555-5555-5555-555555555551', 'Reserva y reprogramación', 'pi pi-calendar', '/portal/reservas', 53, true),
    ('55555555-5555-5555-5555-555555555555', '55555555-5555-5555-5555-555555555551', 'Confirmaciones y recordatorios', 'pi pi-bell', '/portal/notificaciones', 54, true),
    ('55555555-5555-5555-5555-555555555556', '55555555-5555-5555-5555-555555555551', 'Pagos online de cita', 'pi pi-credit-card', '/portal/pagos', 55, true);

-- Prescripción y Medicación
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('66666666-6666-6666-6666-666666666661', NULL, 'Prescripción y Medicación', 'pi pi-clipboard', NULL, 60, true),
    ('66666666-6666-6666-6666-666666666662', '66666666-6666-6666-6666-666666666661', 'Recetas', 'pi pi-file-edit', '/prescripciones/recetas', 61, true),
    ('66666666-6666-6666-6666-666666666663', '66666666-6666-6666-6666-666666666661', 'Control de dispensación', 'pi pi-check-square', '/prescripciones/dispensacion', 62, true),
    ('66666666-6666-6666-6666-666666666664', '66666666-6666-6666-6666-666666666661', 'Kardex por paciente', 'pi pi-briefcase', '/prescripciones/kardex', 63, true);

-- Exámenes y Resultados
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('77777777-7777-7777-7777-777777777771', NULL, 'Exámenes y Resultados', 'pi pi-chart-line', NULL, 70, true),
    ('77777777-7777-7777-7777-777777777772', '77777777-7777-7777-7777-777777777771', 'Órdenes de examen', 'pi pi-file-import', '/examenes/ordenes', 71, true),
    ('77777777-7777-7777-7777-777777777773', '77777777-7777-7777-7777-777777777771', 'Seguimiento de estado', 'pi pi-clock', '/examenes/estado', 72, true),
    ('77777777-7777-7777-7777-777777777774', '77777777-7777-7777-7777-777777777771', 'Registro de resultados', 'pi pi-file', '/examenes/resultados', 73, true),
    ('77777777-7777-7777-7777-777777777775', '77777777-7777-7777-7777-777777777771', 'Validación y firma profesional', 'pi pi-check', '/examenes/validacion', 74, true);

-- Facturación y Caja
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('88888888-8888-8888-8888-888888888881', NULL, 'Facturación y Caja', 'pi pi-wallet', NULL, 80, true),
    ('88888888-8888-8888-8888-888888888882', '88888888-8888-8888-8888-888888888881', 'Proformas / presupuestos', 'pi pi-file-export', '/facturacion/proformas', 81, true),
    ('88888888-8888-8888-8888-888888888883', '88888888-8888-8888-8888-888888888881', 'Comprobantes (boletas / facturas)', 'pi pi-file', '/facturacion/comprobantes', 82, true),
    ('88888888-8888-8888-8888-888888888884', '88888888-8888-8888-8888-888888888881', 'Pagos', 'pi pi-dollar', '/facturacion/pagos', 83, true),
    ('88888888-8888-8888-8888-888888888885', '88888888-8888-8888-8888-888888888881', 'Notas de crédito / devoluciones', 'pi pi-refresh', '/facturacion/notas-credito', 84, true),
    ('88888888-8888-8888-8888-888888888886', '88888888-8888-8888-8888-888888888881', 'Cierre de caja / arqueo', 'pi pi-briefcase', '/facturacion/cierre-caja', 85, true);

-- Inventario y Farmacia
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('99999999-9999-9999-9999-999999999991', NULL, 'Inventario y Farmacia', 'pi pi-box', NULL, 90, true),
    ('99999999-9999-9999-9999-999999999992', '99999999-9999-9999-9999-999999999991', 'Control de stock', 'pi pi-list', '/inventario/stock', 91, true),
    ('99999999-9999-9999-9999-999999999993', '99999999-9999-9999-9999-999999999991', 'Gestión de lotes y vencimientos', 'pi pi-calendar-times', '/inventario/lotes', 92, true),
    ('99999999-9999-9999-9999-999999999994', '99999999-9999-9999-9999-999999999991', 'Compras a proveedores', 'pi pi-truck', '/inventario/compras', 93, true),
    ('99999999-9999-9999-9999-999999999995', '99999999-9999-9999-9999-999999999991', 'Alertas de stock y vencimiento', 'pi pi-exclamation-triangle', '/inventario/alertas', 94, true);

-- RR.HH. y Empleados
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', NULL, 'RR.HH. y Empleados', 'pi pi-id-card', NULL, 100, true),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Ficha de empleado', 'pi pi-user', '/rrhh/empleados', 101, true),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Horarios y guardias', 'pi pi-calendar', '/rrhh/horarios', 102, true),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Control de asistencia', 'pi pi-check-square', '/rrhh/asistencia', 103, true),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Productividad', 'pi pi-chart-bar', '/rrhh/productividad', 104, true);

-- Atención al Cliente
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', NULL, 'Atención al Cliente', 'pi pi-comments', NULL, 110, true),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Seguimiento de contactos', 'pi pi-phone', '/atencion/contactos', 111, true),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Reconfirmación de citas / no-shows', 'pi pi-calendar-minus', '/atencion/no-shows', 112, true),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb4', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Reclamos e incidencias', 'pi pi-flag', '/atencion/reclamos', 113, true),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb5', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Encuestas de satisfacción', 'pi pi-star', '/atencion/encuestas', 114, true);

-- Reportes y Analítica
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('cccccccc-cccc-cccc-cccc-ccccccccccc1', NULL, 'Reportes y Analítica', 'pi pi-chart-bar', NULL, 120, true),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'Reportes operativos', 'pi pi-chart-line', '/reportes/operativos', 121, true),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'Reportes clínicos', 'pi pi-heart', '/reportes/clinicos', 122, true),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc4', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'Reportes financieros', 'pi pi-dollar', '/reportes/financieros', 123, true),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc5', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'Reportes de inventario', 'pi pi-box', '/reportes/inventario', 124, true);

-- Configuración e Integraciones
INSERT INTO menu_items (id, parent_id, label, icon, route, order_index, is_active) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddddd1', NULL, 'Configuración e Integraciones', 'pi pi-cog', NULL, 130, true),
    ('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'dddddddd-dddd-dddd-dddd-ddddddddddd1', 'Plantillas', 'pi pi-file', '/configuracion/plantillas', 131, true),
    ('dddddddd-dddd-dddd-dddd-ddddddddddd3', 'dddddddd-dddd-dddd-dddd-ddddddddddd1', 'Notificaciones (WhatsApp / SMS / email)', 'pi pi-bell', '/configuracion/notificaciones', 132, true),
    ('dddddddd-dddd-dddd-dddd-ddddddddddd4', 'dddddddd-dddd-dddd-dddd-ddddddddddd1', 'Pasarela de pagos', 'pi pi-credit-card', '/configuracion/pagos', 133, true),
    ('dddddddd-dddd-dddd-dddd-ddddddddddd5', 'dddddddd-dddd-dddd-dddd-ddddddddddd1', 'Exportaciones (Excel / PDF)', 'pi pi-file-excel', '/configuracion/exportaciones', 134, true),
    ('dddddddd-dddd-dddd-dddd-ddddddddddd6', 'dddddddd-dddd-dddd-dddd-ddddddddddd1', 'API / Integraciones', 'pi pi-code', '/configuracion/api', 135, true);

-- Asociar todos los ítems al rol ADMIN
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id
FROM roles r
JOIN menu_items mi ON 1 = 1
WHERE r.code = 'ADMIN';

