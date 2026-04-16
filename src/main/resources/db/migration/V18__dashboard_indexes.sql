-- ─── ÍNDICES PARA DASHBOARD ──────────────────────────────────────────────────
-- Mejoran el performance de las queries analíticas del dashboard.

-- Facturas pagadas: filtro por sede + año + status (cubre Revenue Updates y Summary)
CREATE INDEX IF NOT EXISTS idx_invoices_sede_status_created
    ON invoices (sede_id, status, created_at DESC);

-- Payments ordenados por fecha (cubre Recent Transactions)
CREATE INDEX IF NOT EXISTS idx_payments_paid_at_desc
    ON payments (paid_at DESC);

-- Citas atendidas por sede (cubre Performance y Summary)
CREATE INDEX IF NOT EXISTS idx_appointments_sede_start_status
    ON appointments (sede_id, start_time, status);

-- Pacientes nuevos: primera aparición en citas por sede
CREATE INDEX IF NOT EXISTS idx_appointments_patient_sede_start
    ON appointments (patient_id, sede_id, start_time);
