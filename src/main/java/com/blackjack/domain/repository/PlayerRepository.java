package com.blackjack.domain.repository;

import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepository {
    Mono<Player> save(Player player);
    Mono<Player> findById(PlayerId id);
    Mono<Player> findByName(PlayerName name);
    Flux<Player> findAll();
    Flux<Player> findTopByWinRate(int limit);
    Mono<Boolean> existsByName(PlayerName name);
    Mono<Boolean> existsById(PlayerId id);
    Flux<Player> findAllByOrderByWinRateDesc(int limit, int offset);
    Mono<Long> count();

}
