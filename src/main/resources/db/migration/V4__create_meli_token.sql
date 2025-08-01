CREATE TABLE meli_token (
    id SERIAL PRIMARY KEY,
    access_token TEXT NOT NULL,
    refresh_token TEXT NOT NULL,
    expires_in TIMESTAMP NOT NULL
);