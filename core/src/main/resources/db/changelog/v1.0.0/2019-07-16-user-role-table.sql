DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'role') THEN
        CREATE TYPE role AS ENUM ('SYS_ADMIN', 'ADMIN', 'DISPATCHER', 'MANAGER', 'DRIVER', 'COMPANY_OWNER');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role role NOT NULL DEFAULT 'COMPANY_OWNER',
    cancel_date DATE,
    PRIMARY KEY (user_id, role)
);
