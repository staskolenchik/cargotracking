CREATE TABLE IF NOT EXISTS product_owner(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    client_id BIGINT REFERENCES client_company(id)
);
