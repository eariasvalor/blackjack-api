package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.request.CreateGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.game.DeckCount;
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

        DeckCount deckCount = DeckCount.of(request.numberOfDecks());

        return playerRepository.findByName(playerName)
                .switchIfEmpty(Mono.defer(() -> createNewPlayer(playerName)))
                .flatMap(player -> createAndSaveGame(player, deckCount))
                .doOnError(error -> log.error("Error creating game: {}", error.getMessage()));
    }

    private Mono<Player> createNewPlayer(PlayerName playerName) {
        log.debug("Player not found, creating new player: {}", playerName.value());

        Player newPlayer = Player.create(playerName);
        return playerRepository.save(newPlayer)
                .doOnSuccess(player ->
                        log.debug("New player created with ID: {}", player.getId().value())
                );
    }

    private Mono<GameResponse> createAndSaveGame(Player player, DeckCount deckCount) {
        log.debug("Creating game for player: {} with {} decks",
                player.getName().value(),
                deckCount.value());

        Game game = Game.create(player.getId(), deckCount);

        return gameRepository.save(game)
                .map(savedGame -> mapper.toResponse(savedGame, player, deckCount));
    }
}


