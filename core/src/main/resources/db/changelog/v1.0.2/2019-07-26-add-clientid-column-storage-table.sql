

ALTER TABLE storage ADD COLUMN IF NOT EXISTS client_id BIGINT REFERENCES client_company(id);