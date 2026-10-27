-- Manual migration script for local Postgres (demo1)
-- Safe to run multiple times where possible.
-- IMPORTANT: ddl-auto=update will add nullable columns, but if you want NOT NULL/defaults/backfill,
-- run this script once (or keep idempotent blocks).
--
-- Connection example:
--   psql -h localhost -p 5432 -U postgres -d demo1 -f services/backend/src/main/resources/db-migration-manual.sql

BEGIN;

-- 1) Add columns expected by frontend enrichment
ALTER TABLE IF EXISTS products
  ADD COLUMN IF NOT EXISTS category varchar(100),
  ADD COLUMN IF NOT EXISTS minimum integer;

-- 2) Backfill defaults for existing rows (keeps frontend stable)
UPDATE products
SET category = COALESCE(category, 'Tops')
WHERE category IS NULL;

UPDATE products
SET minimum = COALESCE(minimum, 10)
WHERE minimum IS NULL;

-- 3) (Optional but recommended) enforce defaults at DB-level for new rows
ALTER TABLE IF EXISTS products
  ALTER COLUMN category SET DEFAULT 'N/A',
  ALTER COLUMN minimum SET DEFAULT 10;

-- 4) (Optional) enforce NOT NULL after backfill
-- Uncomment only if you are sure you want it mandatory and there are no nulls.
-- ALTER TABLE products
--   ALTER COLUMN category SET NOT NULL,
--   ALTER COLUMN minimum SET NOT NULL;

COMMIT;
