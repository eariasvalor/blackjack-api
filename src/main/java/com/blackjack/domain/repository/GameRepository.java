package com.blackjack.domain.repository;

import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GameRepository {
    Mono<Game> save(Game game);
    Mono<Game> findById(GameId id);
    Mono<Void> deleteById(GameId id);
    Flux<Game> findAllActive();
    Mono<Boolean> existsById(GameId id);
    Mono<Void> deleteByPlayerId(PlayerId playerId);
    Flux<Game> findAll(int page, int size);
    Mono<Long> count();
    Flux<Game> findByPlayerId(PlayerId playerId, int page, int size);
    Mono<Long> countByPlayerId(PlayerId playerId);
}
