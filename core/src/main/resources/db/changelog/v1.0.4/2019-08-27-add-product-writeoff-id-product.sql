ALTER TABLE product ADD COLUMN IF NOT EXISTS product_writeoff_id BIGINT REFERENCES product_writeoff(id);
