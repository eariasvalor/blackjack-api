package com.blackjack.domain.event;

import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.game.GameStatus;
import com.blackjack.domain.model.valueobject.player.PlayerId;

import java.time.LocalDateTime;
import java.util.Objects;

public record GameFinishedEvent(GameId gameId, PlayerId playerId, GameStatus finalStatus,
                                LocalDateTime occurredAt) implements DomainEvent {


    public GameFinishedEvent {
        Objects.requireNonNull(gameId, "GameId cannot be null");
        Objects.requireNonNull(playerId, "PlayerId cannot be null");
        Objects.requireNonNull(finalStatus, "Final status cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");

        if (!finalStatus.isFinished()) {
            throw new IllegalArgumentException("GameFinishedEvent requires a finished status, got: " + finalStatus);
        }
    }

    public static GameFinishedEvent create(GameId gameId, PlayerId playerId, GameStatus finalStatus) {
        return new GameFinishedEvent(gameId, playerId, finalStatus, LocalDateTime.now());
    }
}
