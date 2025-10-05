-- =====================================================================
-- POS Gun Store - Phase 3 schema
-- =====================================================================

-- Extensions (optional but recommended for search)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ---------------------------------------------------------------------
-- Generic trigger to maintain updated_at
-- ---------------------------------------------------------------------
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at := NOW();
RETURN NEW;
END $$;

-- =====================================================================
-- Catalog
-- =====================================================================

-- Categories
CREATE TABLE IF NOT EXISTS categories (
                                          id           BIGSERIAL PRIMARY KEY,
                                          name         TEXT NOT NULL UNIQUE,
                                          version      BIGINT NOT NULL DEFAULT 0,
                                          created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE TRIGGER trg_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Manufacturers
CREATE TABLE IF NOT EXISTS manufacturers (
                                             id           BIGSERIAL PRIMARY KEY,
                                             name         TEXT NOT NULL UNIQUE,
                                             version      BIGINT NOT NULL DEFAULT 0,
                                             created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE TRIGGER trg_manufacturers_updated_at
    BEFORE UPDATE ON manufacturers
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Products
CREATE TABLE IF NOT EXISTS products (
                                        id              BIGSERIAL PRIMARY KEY,
                                        sku             TEXT NOT NULL UNIQUE,
                                        name            TEXT NOT NULL,
                                        description     TEXT,
                                        barcode         TEXT UNIQUE,
                                        category_id     BIGINT REFERENCES categories(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    manufacturer_id BIGINT REFERENCES manufacturers(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    cost            NUMERIC(12,2) NOT NULL DEFAULT 0,
    price           NUMERIC(12,2) NOT NULL DEFAULT 0,
    is_serialized   BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    image_url       TEXT,
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_products_name_trgm ON products USING gin (lower(name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_products_sku_trgm  ON products USING gin (lower(sku)  gin_trgm_ops);
CREATE TRIGGER trg_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =====================================================================
-- Warehouses
-- =====================================================================
CREATE TABLE IF NOT EXISTS warehouses (
                                          id          BIGSERIAL PRIMARY KEY,
                                          name        TEXT NOT NULL,
                                          code        VARCHAR(32) NOT NULL UNIQUE,
    address     TEXT,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    version     BIGINT NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_warehouses_name ON warehouses(name);
CREATE TRIGGER trg_warehouses_updated_at
    BEFORE UPDATE ON warehouses
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =====================================================================
-- Inventory
-- =====================================================================

-- Levels (unique per product+warehouse)
CREATE TABLE IF NOT EXISTS inventory_levels (
                                                id            BIGSERIAL PRIMARY KEY,
                                                product_id    BIGINT NOT NULL REFERENCES products(id)   ON UPDATE RESTRICT ON DELETE RESTRICT,
    warehouse_id  BIGINT NOT NULL REFERENCES warehouses(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    quantity      INTEGER NOT NULL DEFAULT 0,
    reorder_point INTEGER NOT NULL DEFAULT 0,
    version       BIGINT NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_inventory_levels UNIQUE (product_id, warehouse_id)
    );
CREATE INDEX IF NOT EXISTS idx_inventory_levels_product   ON inventory_levels(product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_levels_warehouse ON inventory_levels(warehouse_id);
CREATE TRIGGER trg_inventory_levels_updated_at
    BEFORE UPDATE ON inventory_levels
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Adjustments (+/- qty)
CREATE TABLE IF NOT EXISTS inventory_adjustments (
                                                     id           BIGSERIAL PRIMARY KEY,
                                                     product_id   BIGINT NOT NULL REFERENCES products(id)   ON UPDATE RESTRICT ON DELETE RESTRICT,
    warehouse_id BIGINT NOT NULL REFERENCES warehouses(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    delta        INTEGER NOT NULL,
    reason       TEXT,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_inv_adj_product_created ON inventory_adjustments(product_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_inv_adj_warehouse       ON inventory_adjustments(warehouse_id);

-- Movements (transfers between warehouses)
CREATE TABLE IF NOT EXISTS inventory_movements (
                                                   id            BIGSERIAL PRIMARY KEY,
                                                   product_id    BIGINT NOT NULL REFERENCES products(id)   ON UPDATE RESTRICT ON DELETE RESTRICT,
    from_wh_id    BIGINT NOT NULL REFERENCES warehouses(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    to_wh_id      BIGINT NOT NULL REFERENCES warehouses(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    quantity      INTEGER NOT NULL CHECK (quantity > 0),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_inv_movements_product_created ON inventory_movements(product_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_inv_movements_from_to        ON inventory_movements(from_wh_id, to_wh_id);

-- =====================================================================
-- Serials
-- =====================================================================

-- Enum for serial status
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'serial_status') THEN
CREATE TYPE serial_status AS ENUM ('AVAILABLE','SOLD','DAMAGED');
END IF;
END$$;

-- Serials
CREATE TABLE IF NOT EXISTS serials (
                                       id            BIGSERIAL PRIMARY KEY,
                                       product_id    BIGINT NOT NULL REFERENCES products(id)   ON UPDATE RESTRICT ON DELETE RESTRICT,
    serial_number VARCHAR(128) NOT NULL UNIQUE,
    status        serial_status NOT NULL DEFAULT 'AVAILABLE',
    warehouse_id  BIGINT REFERENCES warehouses(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    version       BIGINT NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_serials_product   ON serials(product_id);
CREATE INDEX IF NOT EXISTS idx_serials_warehouse ON serials(warehouse_id);
CREATE TRIGGER trg_serials_updated_at
    BEFORE UPDATE ON serials
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Serial history
CREATE TABLE IF NOT EXISTS serial_history (
                                              id        BIGSERIAL PRIMARY KEY,
                                              serial_id BIGINT NOT NULL REFERENCES serials(id) ON UPDATE RESTRICT ON DELETE CASCADE,
    at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    action    VARCHAR(64) NOT NULL,
    details   TEXT
    );
CREATE INDEX IF NOT EXISTS idx_serial_history_serial_at ON serial_history(serial_id, at DESC);

-- Compliance events
CREATE TABLE IF NOT EXISTS serial_compliance_events (
                                                        id        BIGSERIAL PRIMARY KEY,
                                                        serial_id BIGINT NOT NULL REFERENCES serials(id) ON UPDATE RESTRICT ON DELETE CASCADE,
    at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    type      VARCHAR(64) NOT NULL,
    payload   TEXT
    );
CREATE INDEX IF NOT EXISTS idx_serial_compliance_serial_at ON serial_compliance_events(serial_id, at DESC);

-- =====================================================================
-- Alerts / Subscriptions
-- =====================================================================

-- Enums for alert types (kept tiny for forward-compat)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'alert_type') THEN
CREATE TYPE alert_type AS ENUM ('LOW_STOCK');
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'subscription_type') THEN
CREATE TYPE subscription_type AS ENUM ('EMAIL','WEBHOOK');
END IF;
END$$;

CREATE TABLE IF NOT EXISTS alert_subscriptions (
                                                   id                 BIGSERIAL PRIMARY KEY,
                                                   alert_type         alert_type NOT NULL,
                                                   subscription_type  subscription_type NOT NULL,
                                                   target             TEXT NOT NULL,       -- email address or webhook URL
                                                   active             BOOLEAN NOT NULL DEFAULT TRUE,
                                                   created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_alert_subs_type_active ON alert_subscriptions(alert_type, active);

-- =====================================================================
-- App-level Configuration (JSON)
-- =====================================================================
CREATE TABLE IF NOT EXISTS app_config (
                                          key         VARCHAR(64) PRIMARY KEY,
    value_json  JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE TRIGGER trg_app_config_updated_at
    BEFORE UPDATE ON app_config
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Seed optional defaults (idempotent)
INSERT INTO app_config(key, value_json)
VALUES
    ('tax',       '{"rate":0.00,"taxInclusive":false}'),
    ('inventory', '{"allowNegativeStock":false,"defaultReorderPoint":0,"lowStockThreshold":0}')
    ON CONFLICT (key) DO NOTHING;

-- =====================================================================
-- Storage / Files (from your existing FileStorageService)
-- =====================================================================

-- Product images (public URLs)
CREATE TABLE IF NOT EXISTS product_images (
                                              id          BIGSERIAL PRIMARY KEY,
                                              file_name   TEXT NOT NULL,
                                              category    TEXT,
                                              url         TEXT NOT NULL,
                                              size_kb     BIGINT,
                                              status      TEXT,
                                              uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_product_images_category ON product_images(category);

-- Import history
CREATE TABLE IF NOT EXISTS import_history (
                                              id          BIGSERIAL PRIMARY KEY,
                                              file_name   TEXT NOT NULL,
                                              type        TEXT NOT NULL,     -- e.g. Products | Customers | Inventory
                                              records     INTEGER NOT NULL DEFAULT 0,
                                              status      TEXT NOT NULL,     -- success | partial | failure
                                              imported_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_import_history_type_when ON import_history(type, imported_at DESC);

-- Backup history
CREATE TABLE IF NOT EXISTS backup_history (
                                              id         BIGSERIAL PRIMARY KEY,
                                              file_name  TEXT NOT NULL,
                                              size_kb    BIGINT,
                                              created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_backup_history_when ON backup_history(created_at DESC);

-- =====================================================================
-- Helpful views (optional)
-- =====================================================================

-- Inventory valuation view (matches /api/reports/inventory-valuation semantics)
CREATE OR REPLACE VIEW v_inventory_valuation AS
SELECT
    p.id                 AS product_id,
    p.sku,
    p.name,
    COALESCE(SUM(il.quantity),0) AS quantity,
    p.cost,
    COALESCE(SUM(il.quantity),0) * p.cost AS value
FROM products p
    LEFT JOIN inventory_levels il ON il.product_id = p.id
GROUP BY p.id, p.sku, p.name, p.cost;

-- Low stock convenience view (uses per-row reorder_point)
CREATE OR REPLACE VIEW v_low_stock AS
SELECT
    il.product_id,
    il.warehouse_id,
    il.quantity,
    il.reorder_point
FROM inventory_levels il
WHERE il.quantity <= il.reorder_point;

-- =====================================================================
-- Index hints for common queries
-- =====================================================================
CREATE INDEX IF NOT EXISTS idx_products_barcode ON products(barcode);
CREATE INDEX IF NOT EXISTS idx_serials_serial_trgm ON serials USING gin (serial_number gin_trgm_ops);

-- Done.