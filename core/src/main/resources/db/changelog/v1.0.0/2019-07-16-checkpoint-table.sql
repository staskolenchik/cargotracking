CREATE TABLE IF NOT EXISTS checkpoint(
    id BIGSERIAL PRIMARY KEY,
    address VARCHAR(40) NOT NULL,
    required_arrival_date TIMESTAMP NOT NULL,
    checkpoint_date TIMESTAMP,
    waybill_id BIGINT REFERENCES waybill(id)
);
