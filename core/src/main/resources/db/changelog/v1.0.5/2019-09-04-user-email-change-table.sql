CREATE TABLE IF NOT EXISTS user_email_change(
    user_id BIGINT NOT NULL PRIMARY KEY,
    uid VARCHAR(255) NOT NULL,
    exp_date TIMESTAMP NOT NULL DEFAULT now() + INTERVAL '30 min',
    new_email VARCHAR(50) NOT NULL
);
