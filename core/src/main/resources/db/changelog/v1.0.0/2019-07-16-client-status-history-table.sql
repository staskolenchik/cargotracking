DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'client_status') THEN
        CREATE TYPE client_status AS ENUM('ACTIVATED', 'INACTIVED');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS client_status_history(
    id BIGSERIAL PRIMARY KEY,
    status client_status NOT NULL DEFAULT 'ACTIVATED',
    date date,
    client_id BIGINT REFERENCES client_company(id)
);
