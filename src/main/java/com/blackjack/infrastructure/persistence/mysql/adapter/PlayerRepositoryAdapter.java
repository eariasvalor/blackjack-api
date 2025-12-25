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
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class PlayerRepositoryAdapter implements PlayerRepository {

    private final PlayerR2dbcRepository r2dbcRepository;
    private final PlayerEntityMapper mapper;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Player> save(Player player) {
        log.debug("Saving player to MySQL: {}", player.getId().value());

        return r2dbcRepository.existsById(player.getId().value())
                .flatMap(exists -> {
                    if (exists) {
                        // Player EXISTE → UPDATE
                        log.debug("Player exists, performing UPDATE: {}", player.getId().value());
                        return updatePlayer(player);
                    } else {
                        // Player NO EXISTE → INSERT usando SQL directo
                        log.debug("Player does not exist, performing INSERT: {}", player.getId().value());
                        return insertPlayer(player);
                    }
                })
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

    private Mono<Player> insertPlayer(Player player) {
        String sql = """
            INSERT INTO players (id, name, games_played, games_won, games_lost, games_tied, win_rate, created_at, updated_at)
            VALUES (:id, :name, :gamesPlayed, :gamesWon, :gamesLost, :gamesTied, :winRate, :createdAt, :updatedAt)
            """;

        return databaseClient.sql(sql)
                .bind("id", player.getId().value())
                .bind("name", player.getName().value())
                .bind("gamesPlayed", player.getGamesPlayed())
                .bind("gamesWon", player.getGamesWon())
                .bind("gamesLost", player.getGamesLost())
                .bind("gamesTied", player.getGamesTied())
                .bind("winRate", player.getWinRate())
                .bind("createdAt", player.getCreatedAt())
                .bind("updatedAt", player.getUpdatedAt())
                .fetch()
                .rowsUpdated()
                .thenReturn(player);
    }

    private Mono<Player> updatePlayer(Player player) {
        String sql = """
            UPDATE players 
            SET name = :name, 
                games_played = :gamesPlayed, 
                games_won = :gamesWon, 
                games_lost = :gamesLost, 
                games_tied = :gamesTied, 
                win_rate = :winRate, 
                updated_at = :updatedAt
            WHERE id = :id
            """;

        return databaseClient.sql(sql)
                .bind("id", player.getId().value())
                .bind("name", player.getName().value())
                .bind("gamesPlayed", player.getGamesPlayed())
                .bind("gamesWon", player.getGamesWon())
                .bind("gamesLost", player.getGamesLost())
                .bind("gamesTied", player.getGamesTied())
                .bind("winRate", player.getWinRate())
                .bind("updatedAt", player.getUpdatedAt())
                .fetch()
                .rowsUpdated()
                .thenReturn(player);
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