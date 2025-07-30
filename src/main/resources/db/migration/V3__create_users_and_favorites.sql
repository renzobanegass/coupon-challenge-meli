
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    nickname VARCHAR(255) NOT NULL,
    site_status VARCHAR(50) NOT NULL
);

CREATE TABLE favorites (
    user_id BIGINT NOT NULL,
    item_id UUID NOT NULL,
    PRIMARY KEY (user_id, item_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);
