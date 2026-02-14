-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS game_results;
DROP TABLE IF EXISTS game_sessions;
DROP TABLE IF EXISTS custom_games;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS group_members;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS users;

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

-- Groups table
CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Group Members table (Many-to-Many relationship between Groups and Users)
CREATE TABLE group_members (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (group_id, user_id)
);

-- Games table (board game types)
CREATE TABLE games (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    min_players INT NOT NULL DEFAULT 1,
    max_players INT NOT NULL DEFAULT 10,
    score_strategy VARCHAR(20) NOT NULL DEFAULT 'HIGH_WIN',
    created_at TIMESTAMP NOT NULL
);

-- Custom Games table (per-group custom game types)
CREATE TABLE custom_games (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    min_players INT NOT NULL DEFAULT 1,
    max_players INT NOT NULL DEFAULT 10,
    score_strategy VARCHAR(20) NOT NULL DEFAULT 'HIGH_WIN',
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    UNIQUE (group_id, name)
);

-- Game Sessions table (a single play session within a group)
CREATE TABLE game_sessions (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    game_id BIGINT,
    custom_game_id BIGINT,
    played_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (game_id) REFERENCES games(id),
    FOREIGN KEY (custom_game_id) REFERENCES custom_games(id),
    CONSTRAINT chk_game_ref CHECK (
        (game_id IS NOT NULL AND custom_game_id IS NULL) OR
        (game_id IS NULL AND custom_game_id IS NOT NULL)
    )
);

CREATE INDEX idx_game_sessions_group_id ON game_sessions(group_id);

-- Game Results table (per-user result in a session)
CREATE TABLE game_results (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    score INT,
    won BOOLEAN NOT NULL DEFAULT FALSE,
    rank INT NOT NULL,
    FOREIGN KEY (session_id) REFERENCES game_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (session_id, user_id)
);
