CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       gender VARCHAR(50),
                       age INTEGER,
                       height DOUBLE PRECISION,
                       weight DOUBLE PRECISION,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);