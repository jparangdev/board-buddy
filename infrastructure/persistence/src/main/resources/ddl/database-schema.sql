-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(50) NOT NULL,
    discriminator VARCHAR(4) NOT NULL,
    provider VARCHAR(20) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    UNIQUE (provider, provider_id),
    UNIQUE (nickname, discriminator)
);

CREATE INDEX idx_nickname_discriminator ON users(nickname, discriminator);
