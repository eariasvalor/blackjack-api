package com.blackjack.domain.model.valueobject.card;

public enum Suit {

    HEARTS("♥", "Hearts"),
    DIAMONDS("♦", "Diamonds"),
    CLUBS("♣", "Clubs"),
    SPADES("♠", "Spades");

    private final String symbol;
    private final String displayName;

    Suit(String symbol, String displayName) {
        this.symbol = symbol;
        this.displayName = displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRed() {
        return this == HEARTS || this == DIAMONDS;
    }

    public boolean isBlack() {
        return this == CLUBS || this == SPADES;
    }
}
