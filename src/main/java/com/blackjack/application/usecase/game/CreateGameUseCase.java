package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.request.CreateGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateGameUseCase {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameResponseMapper mapper;


    public Mono<GameResponse> execute(CreateGameRequest request) {
        log.info("Creating new game for player: {}", request.playerName());

        PlayerName playerName = new PlayerName(request.playerName());

        return playerRepository.findByName(playerName)
                .switchIfEmpty(createNewPlayer(playerName))
                .flatMap(player -> createAndSaveGame(player))
                .doOnSuccess(response ->
                        log.info("Game created successfully: {} for player: {}",
                                response.gameId(),
                                response.playerName())
                )
                .doOnError(error ->
                        log.error("Error creating game for player {}: {}",
                                request.playerName(),
                                error.getMessage())
                );
    }

    private Mono<Player> createNewPlayer(PlayerName playerName) {
        log.debug("Player not found, creating new player: {}", playerName.value());

        Player newPlayer = Player.create(playerName);
        return playerRepository.save(newPlayer)
                .doOnSuccess(player ->
                        log.debug("New player created with ID: {}", player.getId().value())
                );
    }

    private Mono<GameResponse> createAndSaveGame(Player player) {
        log.debug("Creating game for player: {} ({})",
                player.getName().value(),
                player.getId().value());

        Game game = Game.create(player.getId());

        return gameRepository.save(game)
                .map(savedGame -> mapper.toResponse(savedGame, player));
    }
}

