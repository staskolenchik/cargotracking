DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'product_status') THEN
        CREATE TYPE product_status AS ENUM ('ACCEPTED', 'VERIFICATION_COMPLETE', 'DELIVERED', 'LOST');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    amount SMALLINT NOT NULL,
    status product_status NOT NULL DEFAULT 'ACCEPTED',
    invoice_id BIGINT REFERENCES invoice(id)
);
