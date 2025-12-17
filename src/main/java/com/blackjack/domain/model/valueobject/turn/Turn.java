package com.blackjack.domain.model.valueobject.turn;

import com.blackjack.domain.model.valueobject.card.Card;

import java.time.LocalDateTime;
import java.util.Objects;

public record Turn(
        int turnNumber,
        TurnType type,
        TurnOwner owner,
        Card cardDrawn,
        int handValue,
        LocalDateTime timestamp
) {

    public Turn {
        if (turnNumber <= 0) {
            throw new IllegalArgumentException("Turn number must be positive");
        }

        Objects.requireNonNull(type, "Turn type cannot be null");
        Objects.requireNonNull(owner, "Turn owner cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");

        if (handValue < 0) {
            throw new IllegalArgumentException("Hand value cannot be negative");
        }
    }

    public static Turn initialDeal(int turnNumber) {
        return new Turn(
                turnNumber,
                TurnType.INITIAL_DEAL,
                TurnOwner.SYSTEM,
                null,
                0,
                LocalDateTime.now()
        );
    }

    public static Turn playerHit(int turnNumber, Card card, int handValue) {
        Objects.requireNonNull(card, "Card cannot be null for HIT action");
        return new Turn(
                turnNumber,
                TurnType.PLAYER_HIT,
                TurnOwner.PLAYER,
                card,
                handValue,
                LocalDateTime.now()
        );
    }

    public static Turn playerStand(int turnNumber, int handValue) {
        return new Turn(
                turnNumber,
                TurnType.PLAYER_STAND,
                TurnOwner.PLAYER,
                null, handValue,
                LocalDateTime.now()
        );
    }

    public static Turn dealerHit(int turnNumber, Card card, int handValue) {
        Objects.requireNonNull(card, "Card cannot be null for dealer HIT");
        return new Turn(
                turnNumber,
                TurnType.DEALER_HIT,
                TurnOwner.DEALER,
                card,
                handValue,
                LocalDateTime.now()
        );
    }

    public static Turn dealerStand(int turnNumber, int handValue) {
        return new Turn(
                turnNumber,
                TurnType.DEALER_STAND,
                TurnOwner.DEALER,
                null, handValue,
                LocalDateTime.now()
        );
    }

    public boolean hasCard() {
        return cardDrawn != null;
    }

    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(owner.getDisplayName()).append(" - ");
        stringBuilder.append(type.getDisplayName());

        if (hasCard()) {
            stringBuilder.append(": ").append(cardDrawn.getSymbol());
        }

        stringBuilder.append(" (Total: ").append(handValue).append(")");

        return stringBuilder.toString();
    }
}
