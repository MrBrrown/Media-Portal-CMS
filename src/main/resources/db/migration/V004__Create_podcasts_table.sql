CREATE TABLE podcasts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    audio_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

