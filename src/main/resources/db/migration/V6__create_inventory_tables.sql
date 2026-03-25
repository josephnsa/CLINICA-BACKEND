-- INVENTARIO Y FARMACIA
CREATE TABLE inventory_items (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    medication_id UUID        REFERENCES medications(id),
    sede_id       UUID        REFERENCES sedes(id),
    stock         INT         NOT NULL DEFAULT 0,
    min_stock     INT         NOT NULL DEFAULT 10,
    UNIQUE (medication_id, sede_id)
);

CREATE TABLE inventory_movements (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id      UUID        REFERENCES inventory_items(id),
    type         VARCHAR(10) NOT NULL CHECK (type IN ('IN','OUT','ADJUST')),
    quantity     INT         NOT NULL,
    lot_number   VARCHAR(50),
    expiry_date  DATE,
    reason       VARCHAR(200),
    reference    VARCHAR(100),
    created_by   UUID        REFERENCES users(id),
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inventory_items_sede_med ON inventory_items(sede_id, medication_id);
CREATE INDEX idx_inventory_movements_item ON inventory_movements(item_id);
