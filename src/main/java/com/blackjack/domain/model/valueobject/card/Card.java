package com.blackjack.domain.model.valueobject.card;

import java.util.Objects;

public record Card(Rank rank, Suit suit) {

    public Card {
        Objects.requireNonNull(rank, "Rank cannot be null");
        Objects.requireNonNull(suit, "Suit cannot be null");
    }

    public int getValue() {
        return rank.getValue();
    }

    public String getSymbol() {
        return rank.getSymbol() + suit.getSymbol();
    }

    public boolean isAce() {
        return rank.isAce();
    }

    public boolean isFaceCard() {
        return rank.isFaceCard();
    }

    public String toDisplayString() {
        return rank.name() + " of " + suit.getDisplayName();
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}