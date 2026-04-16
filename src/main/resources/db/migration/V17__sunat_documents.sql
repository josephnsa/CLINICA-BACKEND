-- ─── DOCUMENTOS SUNAT ────────────────────────────────────────────────────────
-- Almacena el estado de envío de comprobantes electrónicos a SUNAT vía PSE.
CREATE TABLE sunat_documents (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id    UUID         NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    document_type VARCHAR(10)  NOT NULL,   -- 01=factura, 03=boleta
    xml_content   TEXT,
    cdr_content   TEXT,
    ticket        VARCHAR(100),
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                               CHECK (status IN ('PENDING', 'SENT', 'ACCEPTED', 'REJECTED')),
    error_message TEXT,
    sent_at       TIMESTAMP,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sunat_documents_invoice_id ON sunat_documents(invoice_id);
CREATE INDEX idx_sunat_documents_status     ON sunat_documents(status);
