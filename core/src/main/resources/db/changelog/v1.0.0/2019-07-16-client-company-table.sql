DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'company_type') THEN
        CREATE TYPE company_type AS ENUM ('PRIVATE','LEGAL');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS  client_company(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    type company_type NOT NULL DEFAULT 'PRIVATE'
);
