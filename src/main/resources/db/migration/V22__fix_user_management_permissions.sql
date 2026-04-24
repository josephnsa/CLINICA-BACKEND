-- Separate user-management permissions from catalog permissions.
-- Previously UserController and RoleController used CATALOGO_READ, which is
-- assigned to MEDICO, ENFERMERA, RECEPCIONISTA, CAJERO, and FARMACEUTICO,
-- allowing those roles to list all system users — a security issue.

-- 1. Add dedicated user-management permissions
INSERT INTO permissions (code, module, action, description)
VALUES
    ('USUARIOS_READ',  'security', 'read',  'Ver y listar usuarios del sistema'),
    ('USUARIOS_WRITE', 'security', 'write', 'Crear y modificar usuarios del sistema')
ON CONFLICT (code) DO NOTHING;

-- 2. Grant both only to ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ADMIN'
  AND p.code IN ('USUARIOS_READ', 'USUARIOS_WRITE')
ON CONFLICT DO NOTHING;

-- 3. Update api_endpoints table to reflect the corrected permissions
UPDATE api_endpoints SET permission_code = 'USUARIOS_READ'
WHERE path IN ('/api/security/users', '/api/security/roles')
  AND http_method = 'GET';

UPDATE api_endpoints SET permission_code = 'USUARIOS_WRITE'
WHERE path = '/api/security/users'
  AND http_method IN ('POST', 'PUT', 'PATCH');
