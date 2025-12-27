package com.blackjack.domain.event;

import com.blackjack.domain.event.DomainEvent;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.game.GameStatus;
import com.blackjack.domain.model.valueobject.player.PlayerId;

import java.time.LocalDateTime;

public record GameFinishedEvent(
        GameId gameId,
        PlayerId playerId,
        GameStatus finalStatus,
        LocalDateTime occurredAt
) implements DomainEvent {

    public static GameFinishedEvent of(GameId gameId, PlayerId playerId, GameStatus finalStatus) {
        return new GameFinishedEvent(gameId, playerId, finalStatus, LocalDateTime.now());
    }
}