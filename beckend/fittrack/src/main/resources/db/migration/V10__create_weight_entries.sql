
CREATE TABLE weight_entries
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    weight_kg DOUBLE PRECISION NOT NULL,

    entry_date DATE NOT NULL,

    created_at TIMESTAMPTZ NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_weight_entries_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT chk_weight_entries_weight
        CHECK (weight_kg > 0)
);

CREATE INDEX idx_weight_entries_user_date
    ON weight_entries(user_id, entry_date);
