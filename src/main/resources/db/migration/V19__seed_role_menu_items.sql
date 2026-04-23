-- Asignación de menús por rol (MEDICO, ENFERMERA, RECEPCIONISTA, CAJERO, FARMACEUTICO)

-- ─── MÉDICO ────────────────────────────────────────────────────────────────────
-- Maestro Clínico (CATALOGO_READ)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'MEDICO'
  AND mi.id IN (
    '22222222-2222-2222-2222-222222222221',
    '22222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222223',
    '22222222-2222-2222-2222-222222222224',
    '22222222-2222-2222-2222-222222222225',
    '22222222-2222-2222-2222-222222222226'
  )
ON CONFLICT DO NOTHING;

-- Pacientes e Historia Clínica (patients:read/write, HISTORIA_CLINICA)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'MEDICO'
  AND mi.id IN (
    '33333333-3333-3333-3333-333333333331',
    '33333333-3333-3333-3333-333333333332',
    '33333333-3333-3333-3333-333333333333',
    '33333333-3333-3333-3333-333333333334',
    '33333333-3333-3333-3333-333333333335'
  )
ON CONFLICT DO NOTHING;

-- Agenda (citas, triaje, consulta)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'MEDICO'
  AND mi.id IN (
    '44444444-4444-4444-4444-444444444441',
    '44444444-4444-4444-4444-444444444444',
    '44444444-4444-4444-4444-444444444446',
    '44444444-4444-4444-4444-444444444447'
  )
ON CONFLICT DO NOTHING;

-- Prescripción y Medicación
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'MEDICO'
  AND mi.id IN (
    '66666666-6666-6666-6666-666666666661',
    '66666666-6666-6666-6666-666666666662',
    '66666666-6666-6666-6666-666666666664'
  )
ON CONFLICT DO NOTHING;

-- Exámenes y Resultados
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'MEDICO'
  AND mi.id IN (
    '77777777-7777-7777-7777-777777777771',
    '77777777-7777-7777-7777-777777777772',
    '77777777-7777-7777-7777-777777777773',
    '77777777-7777-7777-7777-777777777775'
  )
ON CONFLICT DO NOTHING;

-- ─── ENFERMERA ─────────────────────────────────────────────────────────────────
-- Maestro Clínico
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'ENFERMERA'
  AND mi.id IN (
    '22222222-2222-2222-2222-222222222221',
    '22222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222223',
    '22222222-2222-2222-2222-222222222224',
    '22222222-2222-2222-2222-222222222225',
    '22222222-2222-2222-2222-222222222226'
  )
ON CONFLICT DO NOTHING;

-- Pacientes (registro y ficha clínica)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'ENFERMERA'
  AND mi.id IN (
    '33333333-3333-3333-3333-333333333331',
    '33333333-3333-3333-3333-333333333332',
    '33333333-3333-3333-3333-333333333334'
  )
ON CONFLICT DO NOTHING;

-- Agenda (citas, admisión/check-in, triaje)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'ENFERMERA'
  AND mi.id IN (
    '44444444-4444-4444-4444-444444444441',
    '44444444-4444-4444-4444-444444444444',
    '44444444-4444-4444-4444-444444444445',
    '44444444-4444-4444-4444-444444444446'
  )
ON CONFLICT DO NOTHING;

-- Exámenes (seguimiento y resultados)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'ENFERMERA'
  AND mi.id IN (
    '77777777-7777-7777-7777-777777777771',
    '77777777-7777-7777-7777-777777777773',
    '77777777-7777-7777-7777-777777777774'
  )
ON CONFLICT DO NOTHING;

-- ─── RECEPCIONISTA ─────────────────────────────────────────────────────────────
-- Maestro Clínico
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'RECEPCIONISTA'
  AND mi.id IN (
    '22222222-2222-2222-2222-222222222221',
    '22222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222223',
    '22222222-2222-2222-2222-222222222224',
    '22222222-2222-2222-2222-222222222225',
    '22222222-2222-2222-2222-222222222226'
  )
ON CONFLICT DO NOTHING;

-- Pacientes (registro y consentimientos)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'RECEPCIONISTA'
  AND mi.id IN (
    '33333333-3333-3333-3333-333333333331',
    '33333333-3333-3333-3333-333333333332',
    '33333333-3333-3333-3333-333333333333'
  )
ON CONFLICT DO NOTHING;

-- Agenda (calendarios, citas, admisión)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'RECEPCIONISTA'
  AND mi.id IN (
    '44444444-4444-4444-4444-444444444441',
    '44444444-4444-4444-4444-444444444442',
    '44444444-4444-4444-4444-444444444444',
    '44444444-4444-4444-4444-444444444445'
  )
ON CONFLICT DO NOTHING;

-- Atención al Cliente
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'RECEPCIONISTA'
  AND mi.id IN (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb4',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb5'
  )
ON CONFLICT DO NOTHING;

-- ─── CAJERO ────────────────────────────────────────────────────────────────────
-- Maestro Clínico
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'CAJERO'
  AND mi.id IN (
    '22222222-2222-2222-2222-222222222221',
    '22222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222223',
    '22222222-2222-2222-2222-222222222224',
    '22222222-2222-2222-2222-222222222225',
    '22222222-2222-2222-2222-222222222226'
  )
ON CONFLICT DO NOTHING;

-- Pacientes (solo registro)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'CAJERO'
  AND mi.id IN (
    '33333333-3333-3333-3333-333333333331',
    '33333333-3333-3333-3333-333333333332'
  )
ON CONFLICT DO NOTHING;

-- Facturación y Caja (todo)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'CAJERO'
  AND mi.id IN (
    '88888888-8888-8888-8888-888888888881',
    '88888888-8888-8888-8888-888888888882',
    '88888888-8888-8888-8888-888888888883',
    '88888888-8888-8888-8888-888888888884',
    '88888888-8888-8888-8888-888888888885',
    '88888888-8888-8888-8888-888888888886'
  )
ON CONFLICT DO NOTHING;

-- ─── FARMACÉUTICO ──────────────────────────────────────────────────────────────
-- Maestro Clínico
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'FARMACEUTICO'
  AND mi.id IN (
    '22222222-2222-2222-2222-222222222221',
    '22222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222223',
    '22222222-2222-2222-2222-222222222224',
    '22222222-2222-2222-2222-222222222225',
    '22222222-2222-2222-2222-222222222226'
  )
ON CONFLICT DO NOTHING;

-- Prescripción y Medicación (recetas, dispensación, kardex)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'FARMACEUTICO'
  AND mi.id IN (
    '66666666-6666-6666-6666-666666666661',
    '66666666-6666-6666-6666-666666666662',
    '66666666-6666-6666-6666-666666666663',
    '66666666-6666-6666-6666-666666666664'
  )
ON CONFLICT DO NOTHING;

-- Inventario y Farmacia (todo)
INSERT INTO role_menu_items (role_id, menu_item_id)
SELECT r.id, mi.id FROM roles r, menu_items mi
WHERE r.code = 'FARMACEUTICO'
  AND mi.id IN (
    '99999999-9999-9999-9999-999999999991',
    '99999999-9999-9999-9999-999999999992',
    '99999999-9999-9999-9999-999999999993',
    '99999999-9999-9999-9999-999999999994',
    '99999999-9999-9999-9999-999999999995'
  )
ON CONFLICT DO NOTHING;
