package com.blackjack.infrastructure.persistence.mongodb.repository;

import com.blackjack.infrastructure.persistence.mongodb.document.GameDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface GameMongoRepository extends ReactiveMongoRepository<GameDocument, String> {

    Flux<GameDocument> findByPlayerId(String playerId);

    Flux<GameDocument> findByStatusNot(String status);
}
