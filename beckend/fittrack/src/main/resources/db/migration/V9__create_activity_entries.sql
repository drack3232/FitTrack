
CREATE TABLE activity_entries
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    activity_type VARCHAR(30) NOT NULL,

    entry_date DATE NOT NULL,

    duration_minutes INTEGER NOT NULL,

    calories_burned DOUBLE PRECISION NOT NULL,

    created_at TIMESTAMPTZ NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_activity_entries_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT chk_activity_entries_type
        CHECK (
            activity_type IN (
                              'WALKING',
                              'RUNNING',
                              'CYCLING',
                              'SWIMMING',
                              'STRENGTH_TRAINING',
                              'OTHER'
                )
            ),

    CONSTRAINT chk_activity_entries_duration
        CHECK (duration_minutes > 0),

    CONSTRAINT chk_activity_entries_calories
        CHECK (calories_burned >= 0)
);

CREATE INDEX idx_activity_entries_user_date
    ON activity_entries(user_id, entry_date);
