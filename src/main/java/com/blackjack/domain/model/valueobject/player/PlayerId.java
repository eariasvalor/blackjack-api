package com.blackjack.domain.model.valueobject.player;

import java.util.Objects;
import java.util.UUID;

public record PlayerId(String value) {

    public PlayerId {
        Objects.requireNonNull(value, "Player ID cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Player ID cannot be empty");
        }
    }

    public static PlayerId generate() {
        return new PlayerId("player-" + UUID.randomUUID());
    }

    public static PlayerId from(String value) {
        return new PlayerId(value);
    }
}