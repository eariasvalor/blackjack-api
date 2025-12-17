package com.blackjack.domain.model.valueobject.turn;

public enum TurnType {

    INITIAL_DEAL("Initial deal"),

    PLAYER_HIT("Player hits"),

    PLAYER_STAND("Player stands"),

    DEALER_HIT("Dealer hits"),

    DEALER_STAND("Dealer stands");

    private final String displayName;

    TurnType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}