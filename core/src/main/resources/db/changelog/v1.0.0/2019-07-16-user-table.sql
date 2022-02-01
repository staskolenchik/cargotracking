CREATE TABLE IF NOT EXISTS "user"(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20),
    surname VARCHAR(20) NOT NULL,
    patronymic VARCHAR(20),
    born_date DATE,
    email VARCHAR(50),
    town VARCHAR(20),
    street VARCHAR(20),
    house VARCHAR(5),
    flat SMALLINT,
    login VARCHAR(15) NOT NULL,
    password VARCHAR(50) NOT NULL,
    passport_num VARCHAR(30),
    issued_by VARCHAR(50),
    client_id BIGINT REFERENCES client_company(id)
);
