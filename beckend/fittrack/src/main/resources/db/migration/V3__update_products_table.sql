
ALTER TABLE products
    ADD COLUMN barcode VARCHAR(14),
    ADD COLUMN owner_id BIGINT;

DO $$
DECLARE
existing_constraint RECORD;
BEGIN
FOR existing_constraint IN
SELECT c.conname
FROM pg_constraint AS c
WHERE c.conrelid = 'products'::regclass
          AND c.contype = 'u'
          AND c.conkey = ARRAY[
              (
                  SELECT a.attnum
                  FROM pg_attribute AS a
                  WHERE a.attrelid = 'products'::regclass
                    AND a.attname = 'name'
              )
          ]::SMALLINT[]
    LOOP
        EXECUTE format(
            'ALTER TABLE products DROP CONSTRAINT %I',
            existing_constraint.conname
        );
END LOOP;
END $$;

ALTER TABLE products
    ADD CONSTRAINT fk_products_owner
        FOREIGN KEY (owner_id)
            REFERENCES users (id);

CREATE UNIQUE INDEX uq_products_barcode
    ON products (barcode)
    WHERE barcode IS NOT NULL;

CREATE INDEX idx_products_owner_id
    ON products (owner_id);
