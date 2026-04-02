-- Permiso para confirmar citas (PENDING -> CONFIRMED)
INSERT INTO permissions (code, module, action, description)
VALUES ('agenda:confirm', 'agenda', 'confirm', 'Confirmar citas')
ON CONFLICT (code) DO NOTHING;

-- ADMIN: agregar permiso de confirmar
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ADMIN'
  AND p.code = 'agenda:confirm'
ON CONFLICT DO NOTHING;

-- RECEPCIONISTA: agregar permiso de confirmar
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'RECEPCIONISTA'
  AND p.code = 'agenda:confirm'
ON CONFLICT DO NOTHING;
