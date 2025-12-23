DROP TABLE IF EXISTS players;

CREATE TABLE IF NOT EXISTS players (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    games_lost INT DEFAULT 0,
    games_tied INT DEFAULT 0,
    win_rate DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE INDEX idx_players_name ON players(name);

CREATE INDEX idx_players_win_rate ON players(win_rate DESC);