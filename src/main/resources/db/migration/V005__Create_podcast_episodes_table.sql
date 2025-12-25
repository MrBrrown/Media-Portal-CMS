CREATE TABLE podcast_episodes (
    podcast_id BIGINT NOT NULL,
    episode VARCHAR(255) NOT NULL,
    CONSTRAINT fk_podcast_episodes_podcast FOREIGN KEY (podcast_id) REFERENCES podcasts(id) ON DELETE CASCADE,
    PRIMARY KEY (podcast_id, episode)
);

CREATE INDEX idx_podcast_episodes_podcast_id ON podcast_episodes(podcast_id);

