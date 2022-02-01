CREATE TABLE IF NOT EXISTS refresh_token(
    user_id BIGINT NOT NULL,
    ip VARCHAR(25) NOT NULL,
    token VARCHAR(255),
    PRIMARY KEY (user_id, ip)
);
