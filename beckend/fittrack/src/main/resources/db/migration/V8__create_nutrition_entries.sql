
CREATE TABLE food_entries
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    product_name VARCHAR(255) NOT NULL,

    meal_type VARCHAR(20) NOT NULL,
    entry_date DATE NOT NULL,

    quantity_grams DOUBLE PRECISION NOT NULL,

    calories DOUBLE PRECISION NOT NULL,
    protein_grams DOUBLE PRECISION NOT NULL,
    fat_grams DOUBLE PRECISION NOT NULL,
    carb_grams DOUBLE PRECISION NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_food_entries_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT chk_food_entries_meal_type
        CHECK (
            meal_type IN (
                          'BREAKFAST',
                          'LUNCH',
                          'DINNER',
                          'SNACK'
                )
            ),

    CONSTRAINT chk_food_entries_quantity
        CHECK (quantity_grams > 0),

    CONSTRAINT chk_food_entries_calories
        CHECK (calories >= 0),

    CONSTRAINT chk_food_entries_protein
        CHECK (protein_grams >= 0),

    CONSTRAINT chk_food_entries_fat
        CHECK (fat_grams >= 0),

    CONSTRAINT chk_food_entries_carbs
        CHECK (carb_grams >= 0),

    CONSTRAINT chk_food_entries_product_name
        CHECK (LENGTH(TRIM(product_name)) > 0)
);

CREATE INDEX idx_food_entries_user_date
    ON food_entries(user_id, entry_date);



CREATE TABLE water_entries
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    amount_ml INTEGER NOT NULL,
    entry_date DATE NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_water_entries_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT chk_water_entries_amount
        CHECK (amount_ml > 0)
);

CREATE INDEX idx_water_entries_user_date
    ON water_entries(user_id, entry_date);
