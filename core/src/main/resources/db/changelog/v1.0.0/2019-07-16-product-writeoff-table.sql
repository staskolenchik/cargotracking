DO $$
begin
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'product_writeoff_enum') THEN
        CREATE TYPE product_writeoff_enum AS ENUM ('LOST', 'STOLEN', 'SPOILED');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS product_writeoff (
    id BIGSERIAL PRIMARY KEY,
    amount SMALLINT NOT NULL,
    status product_writeoff_enum NOT NULL DEFAULT 'LOST',
    product_id BIGINT REFERENCES product(id),
    created_by BIGINT REFERENCES "user"(id)
);
