DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'carriage_status') THEN
        CREATE TYPE carriage_status AS ENUM ('STARTED_CARRIAGE', 'FINISHED_CARRIAGE');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS waybill(
    id BIGSERIAL PRIMARY KEY,
    start_point VARCHAR(40) NOT NULL,
    end_point VARCHAR(40) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status carriage_status NOT NULL DEFAULT 'STARTED_CARRIAGE',
    car_id BIGINT REFERENCES car(id),
    created_by BIGINT REFERENCES "user"(id),
    invoice_id BIGINT REFERENCES invoice(id)
);
