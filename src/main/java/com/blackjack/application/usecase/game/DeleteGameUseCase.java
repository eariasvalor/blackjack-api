package com.blackjack.application.usecase.game;

import com.blackjack.application.exception.GameNotFoundException;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteGameUseCase {

    private final GameRepository gameRepository;

    public Mono<Void> execute(String gameId) {
        log.info("Deleting game with id: {}", gameId);

        return gameRepository.findById(GameId.from(gameId))
                .switchIfEmpty(Mono.error(new GameNotFoundException(gameId)))
                .flatMap(game -> gameRepository.deleteById(game.getId()))
                .doOnSuccess(v -> log.info("Game deleted successfully: {}", gameId))
                .doOnError(error -> log.error("Error deleting the game: {}", gameId, error.getMessage()));
    }
}