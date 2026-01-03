package com.blackjack.domain.model.valueobject.game;

public record DeckCount(int value) {

    private static final int MIN_DECKS = 1;
    private static final int MAX_DECKS = 8;

    public DeckCount {
        if (value < MIN_DECKS || value > MAX_DECKS) {
            throw new IllegalArgumentException(
                    "Deck count must be between " + MIN_DECKS + " and " + MAX_DECKS
            );
        }
    }

    public static DeckCount standard() {
        return new DeckCount(1);
    }

    public static DeckCount of(Integer value) {
        return new DeckCount(value != null ? value : 1);
    }
}