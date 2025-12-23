package com.blackjack.infrastructure.persistence.mysql.adapter;

import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.domain.repository.PlayerRepository;
import com.blackjack.infrastructure.persistence.mysql.entity.PlayerEntity;
import com.blackjack.infrastructure.persistence.mysql.mapper.PlayerEntityMapper;
import com.blackjack.infrastructure.persistence.mysql.repository.PlayerR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class PlayerRepositoryAdapter implements PlayerRepository {

    private final PlayerR2dbcRepository r2dbcRepository;
    private final PlayerEntityMapper mapper;

    @Override
    public Mono<Player> save(Player player) {
        log.debug("Saving player to MySQL: {}", player.getId().value());

        PlayerEntity entity = mapper.toEntity(player);

        return r2dbcRepository.save(entity)
                .map(mapper::toDomain)
                .doOnSuccess(saved ->
                        log.debug("Player saved successfully: {} ({})",
                                saved.getName().value(),
                                saved.getId().value())
                )
                .doOnError(error ->
                        log.error("Error saving player {}: {}",
                                player.getId().value(),
                                error.getMessage())
                );
    }

    @Override
    public Mono<Player> findById(PlayerId id) {
        log.debug("Finding player by id in MySQL: {}", id.value());

        return r2dbcRepository.findById(id.value())
                .map(mapper::toDomain)
                .doOnSuccess(player -> {
                    if (player != null) {
                        log.debug("Player found: {} ({})",
                                player.getName().value(),
                                player.getId().value());
                    } else {
                        log.debug("Player not found: {}", id.value());
                    }
                });
    }

    @Override
    public Mono<Player> findByName(PlayerName name) {
        log.debug("Finding player by name in MySQL: {}", name.value());

        return r2dbcRepository.findByNameIgnoreCase(name.value())
                .map(mapper::toDomain)
                .doOnSuccess(player -> {
                    if (player != null) {
                        log.debug("Player found by name: {} ({})",
                                player.getName().value(),
                                player.getId().value());
                    } else {
                        log.debug("Player not found by name: {}", name.value());
                    }
                });
    }

    @Override
    public Flux<Player> findAll() {
        log.debug("Finding all players in MySQL");

        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnComplete(() ->
                        log.debug("Completed finding all players")
                );
    }

    @Override
    public Flux<Player> findTopByWinRate(int limit) {
        log.debug("Finding top {} players by win rate in MySQL", limit);

        return r2dbcRepository.findTopByWinRate(limit)
                .map(mapper::toDomain)
                .doOnComplete(() ->
                        log.debug("Completed finding top players")
                );
    }

    @Override
    public Mono<Boolean> existsByName(PlayerName name) {
        log.debug("Checking if player exists by name in MySQL: {}", name.value());

        return r2dbcRepository.existsByNameIgnoreCase(name.value())
                .doOnSuccess(exists ->
                        log.debug("Player with name {} exists: {}", name.value(), exists)
                );
    }

    @Override
    public Mono<Boolean> existsById(PlayerId id) {
        log.debug("Checking if player exists by id in MySQL: {}", id.value());

        return r2dbcRepository.existsById(id.value())
                .doOnSuccess(exists ->
                        log.debug("Player {} exists: {}", id.value(), exists)
                );
    }

    @Override
    public Mono<Long> count() {
        log.debug("Counting players in MySQL");

        return r2dbcRepository.count()
                .doOnSuccess(count ->
                        log.debug("Total players: {}", count)
                );
    }
}