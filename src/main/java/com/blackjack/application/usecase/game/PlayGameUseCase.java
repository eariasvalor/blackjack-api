package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.request.PlayGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.exception.GameNotFoundException;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayGameUseCase {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameResponseMapper mapper;

    public Mono<GameResponse> execute(String gameId, PlayGameRequest request) {
        log.info("Executing action {} for game: {}", request.action(), gameId);

        return gameRepository.findById(GameId.from(gameId))
                .switchIfEmpty(Mono.error(new GameNotFoundException(gameId)))
                .flatMap(game -> executeAction(game, request.action()))
                .flatMap(gameRepository::save)
                .flatMap(game -> playerRepository.findById(game.getPlayerId())
                        .map(player -> mapper.toResponse(game, player)))
                .doOnSuccess(response -> log.info("Action {} executed successfully for game: {}. Status: {}",
                        request.action(), gameId, response.status()))
                .doOnError(error -> log.error("Error executing action {} for game {}: {}",
                        request.action(), gameId, error.getMessage()));
    }

    private Mono<Game> executeAction(Game game, String action) {
        return Mono.fromSupplier(() -> {
            log.debug("Executing action {} on game {}", action, game.getId().value());

            if ("HIT".equalsIgnoreCase(action)) {
                game.hit();
            } else if ("STAND".equalsIgnoreCase(action)) {
                game.stand();
            } else {
                throw new IllegalArgumentException("Invalid action: " + action + ". Valid actions are: HIT, STAND");
            }

            return game;
        });
    }
}