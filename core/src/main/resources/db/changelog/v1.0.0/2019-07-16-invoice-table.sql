DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'invoice_status') THEN
        CREATE TYPE invoice_status AS ENUM ('MADE_OUT', 'VERIFICATION_COMPLETE', 'DELIVERED');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS invoice (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL,
    creation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    verified_date DATE,
    status invoice_status NOT NULL DEFAULT 'MADE_OUT',
    storage_id BIGINT REFERENCES storage(id),
    created_by BIGINT REFERENCES "user"(id),
    verified_by BIGINT REFERENCES "user"(id),
    driver_id BIGINT REFERENCES "user"(id),
    product_owner_id BIGINT REFERENCES product_owner(id)
);
