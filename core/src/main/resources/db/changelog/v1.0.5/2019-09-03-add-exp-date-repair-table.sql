ALTER TABLE repair_account ADD COLUMN IF NOT EXISTS exp_date TIMESTAMP NOT NULL DEFAULT now() + INTERVAL '30 min';
