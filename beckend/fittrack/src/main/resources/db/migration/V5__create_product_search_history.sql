
CREATE TABLE product_search_history
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    query_text VARCHAR(200) NOT NULL,

    normalized_query VARCHAR(200) NOT NULL,

    last_searched_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_product_search_history_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT uk_product_search_user_query
        UNIQUE (user_id, normalized_query)
);

CREATE INDEX idx_product_search_user_last_searched
    ON product_search_history (
                               user_id,
                               last_searched_at DESC
        );
