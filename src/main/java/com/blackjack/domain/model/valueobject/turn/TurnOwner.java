package com.blackjack.domain.model.valueobject.turn;

public enum TurnOwner {

    SYSTEM("System"),

    PLAYER("Player"),

    DEALER("Dealer");

    private final String displayName;

    TurnOwner(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}