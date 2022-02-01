DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'car_type') THEN
        CREATE TYPE car_type AS ENUM ('CISTERN', 'REFRIGERATOR', 'COVERED_BODY');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS car(
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(10) NOT NULL,
    fuel_consumption NUMERIC(10,3) NOT NULL,
    load_capacity INTEGER NOT NULL,
    type car_type NOT NULL DEFAULT 'CISTERN',
    client_company_id BIGINT REFERENCES client_company(id)
);
