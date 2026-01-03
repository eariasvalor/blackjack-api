package com.blackjack.infrastructure.persistence.mongodb.adapter;

import com.blackjack.domain.event.GameFinishedEvent;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.infrastructure.event.DomainEventPublisher;
import com.blackjack.infrastructure.persistence.mongodb.document.GameDocument;
import com.blackjack.infrastructure.persistence.mongodb.mapper.GameDocumentMapper;
import com.blackjack.infrastructure.persistence.mongodb.repository.GameMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class GameRepositoryAdapter implements GameRepository {

    private final GameMongoRepository mongoRepository;
    private final GameDocumentMapper mapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    public Mono<Game> save(Game game) {
        log.debug("Saving game to MongoDB: {}", game.getId().value());

        GameDocument document = mapper.toDocument(game);

        return mongoRepository.save(document)
                .map(mapper::toDomain)
                .doOnSuccess(savedGame -> {
                    log.debug("Game saved successfully: {}", savedGame.getId().value());

                    game.getDomainEvents().forEach(event -> {
                        if (event instanceof GameFinishedEvent gameFinishedEvent) {
                            eventPublisher.publishGameFinishedEvent(gameFinishedEvent);
                        }
                    });
                    game.clearDomainEvents();
                })

                .doOnError(error ->
                        log.error("Error saving game {}: {}", game.getId().value(), error.getMessage())
                );
    }

    @Override
    public Mono<Game> findById(GameId id) {
        log.debug("Finding game by id in MongoDB: {}", id.value());

        return mongoRepository.findById(id.value())
                .map(mapper::toDomain)
                .doOnSuccess(game -> {
                    if (game != null) {
                        log.debug("Game found: {}", game.getId().value());
                    } else {
                        log.debug("Game not found: {}", id.value());
                    }
                });
    }

    @Override
    public Mono<Void> deleteById(GameId id) {
        log.debug("Deleting game from MongoDB: {}", id.value());

        return mongoRepository.deleteById(id.value())
                .doOnSuccess(v ->
                        log.debug("Game deleted: {}", id.value())
                )
                .doOnError(error ->
                        log.error("Error deleting game {}: {}", id.value(), error.getMessage())
                );
    }

    @Override
    public Flux<Game> findByPlayerId(PlayerId playerId) {
        log.debug("Finding games by player id in MongoDB: {}", playerId.value());

        return mongoRepository.findByPlayerId(playerId.value())
                .map(mapper::toDomain)
                .doOnComplete(() ->
                        log.debug("Completed finding games for player: {}", playerId.value())
                );
    }

    @Override
    public Flux<Game> findAllActive() {
        log.debug("Finding all active games in MongoDB");

        return mongoRepository.findByStatusNot("PLAYING")
                .map(mapper::toDomain)
                .doOnComplete(() ->
                        log.debug("Completed finding active games")
                );
    }

    @Override
    public Mono<Boolean> existsById(GameId id) {
        log.debug("Checking if game exists in MongoDB: {}", id.value());

        return mongoRepository.existsById(id.value())
                .doOnSuccess(exists ->
                        log.debug("Game {} exists: {}", id.value(), exists)
                );
    }

    @Override
    public Mono<Void> deleteByPlayerId(PlayerId playerId) {
        log.debug("Deleting all games for player from MongoDB: {}", playerId.value());

        return mongoRepository.deleteByPlayerId(playerId.value())
                .then()
                .doOnSuccess(v ->
                        log.debug("Games deleted for player: {}", playerId.value())
                )
                .doOnError(error ->
                        log.error("Error deleting games for player {}: {}", playerId.value(), error.getMessage())
                );
    }

    @Override
    public Flux<Game> findAll(int page, int size) {
        return mongoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .skip((long) page * size)
                .take(size)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("Retrieved games page"));
    }

    @Override
    public Mono<Long> count() {
        return mongoRepository.count();
    }
}
