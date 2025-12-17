package com.blackjack.domain.model.valueobject.game;

import java.util.Objects;
import java.util.UUID;

public record GameId(String value) {

    public GameId {
        Objects.requireNonNull(value, "Game ID cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Game ID cannot be empty");
        }
    }

    public static GameId generate() {
        return new GameId("game-" + UUID.randomUUID());
    }

    public static GameId from(String value) {
        return new GameId(value);
    }
}
