package com.blackjack.infrastructure.persistence.mysql.repository;

import com.blackjack.infrastructure.persistence.mysql.entity.PlayerEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerR2dbcRepository extends R2dbcRepository<PlayerEntity, String> {

    @Query("SELECT * FROM players WHERE LOWER(name) = LOWER(:name)")
    Mono<PlayerEntity> findByNameIgnoreCase(String name);

    @Query("SELECT * FROM players ORDER BY win_rate DESC LIMIT :limit")
    Flux<PlayerEntity> findTopByWinRate(int limit);

    @Query("SELECT COUNT(*) > 0 FROM players WHERE LOWER(name) = LOWER(:name)")
    Mono<Boolean> existsByNameIgnoreCase(String name);
}