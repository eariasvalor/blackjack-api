package com.blackjack.domain.model.valueobject.player;

import java.util.Objects;

public record PlayerName(String value) {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;
    private static final String VALID_CHARS_REGEX = "^[a-zA-Z0-9\\s_-]+$";

    public PlayerName {
        Objects.requireNonNull(value, "Player name cannot be null");

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty");
        }

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Player name must be at least %d characters long", MIN_LENGTH)
            );
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Player name must not exceed %d characters", MAX_LENGTH)
            );
        }

        if (!value.matches(VALID_CHARS_REGEX)) {
            throw new IllegalArgumentException(
                    "Player name can only contain letters, numbers, spaces, hyphens and underscores"
            );
        }
    }

    public static PlayerName from(String value) {
        return new PlayerName(value);
    }
}