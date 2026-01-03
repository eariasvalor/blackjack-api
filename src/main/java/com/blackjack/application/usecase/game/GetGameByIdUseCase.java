package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.exception.GameNotFoundException;
import com.blackjack.application.mapper.GameResponseMapper;
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
public class GetGameByIdUseCase {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameResponseMapper mapper;


    public Mono<GameResponse> execute(String gameId) {
        log.info("Getting game details for gameId: {}", gameId);

        return gameRepository.findById(GameId.from(gameId))
                .switchIfEmpty(Mono.error(new GameNotFoundException(gameId)))
                .flatMap(game -> playerRepository.findById(game.getPlayerId())
                        .map(player -> mapper.toResponse(game, player, game.getDeck().getDeckCount())))
                .doOnSuccess(response -> log.info("Game found: {}", gameId))
                .doOnError(error -> log.error("Error getting game {}: {}", gameId, error.getMessage()));
    }
}