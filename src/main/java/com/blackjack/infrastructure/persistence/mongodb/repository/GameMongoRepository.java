package com.blackjack.infrastructure.persistence.mongodb.repository;

import com.blackjack.infrastructure.persistence.mongodb.document.GameDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface GameMongoRepository extends ReactiveMongoRepository<GameDocument, String> {

    Flux<GameDocument> findByPlayerId(String playerId, Pageable pageable);

    Flux<GameDocument> findByStatusNot(String status);

    Mono<Long> deleteByPlayerId(String playerId);

    Mono<Long> countByPlayerId(String playerId);
}
