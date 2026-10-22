
CREATE TABLE user_goals
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL UNIQUE,

    goal_type VARCHAR(20) NOT NULL,

    activity_level VARCHAR(30) NOT NULL,

    target_weight DOUBLE PRECISION,

    created_at TIMESTAMPTZ NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_goals_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT chk_user_goals_goal_type
        CHECK (goal_type IN (
                             'LOSE',
                             'MAINTAIN',
                             'GAIN'
            )),

    CONSTRAINT chk_user_goals_activity_level
        CHECK (activity_level IN (
                                  'SEDENTARY',
                                  'LIGHTLY_ACTIVE',
                                  'MODERATELY_ACTIVE',
                                  'VERY_ACTIVE',
                                  'EXTRA_ACTIVE'
            )),

    CONSTRAINT chk_user_goals_target_weight
        CHECK (
            target_weight IS NULL
                OR target_weight > 0
            )
);
