-- Alinear estado de receta con dominio/documentación: COMPLETED -> DISPENSED
UPDATE prescriptions
SET status = 'DISPENSED'
WHERE status = 'COMPLETED';
