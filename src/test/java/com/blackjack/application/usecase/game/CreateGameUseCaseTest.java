package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.request.CreateGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.game.GameStatus;
import com.blackjack.domain.model.valueobject.game.Hand;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateGameUseCase Tests")
class CreateGameUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    private CreateGameUseCase createGameUseCase;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should create game with existing player")
    void shouldCreateGameWithExistingPlayer() {
        CreateGameRequest request = new CreateGameRequest("Juan");
        PlayerName playerName = new PlayerName("Juan");

        Player existingPlayer = mock(Player.class);
        when(existingPlayer.getId()).thenReturn(new PlayerId("player-123"));
        when(existingPlayer.getName()).thenReturn(playerName);

        when(playerRepository.findByName(any(PlayerName.class)))
                .thenReturn(Mono.just(existingPlayer));

        Game mockGame = mock(Game.class);
        when(mockGame.getId()).thenReturn(new GameId("game-456"));
        when(mockGame.getPlayerId()).thenReturn(new PlayerId("player-123"));
        when(mockGame.getStatus()).thenReturn(GameStatus.PLAYING);
        when(mockGame.getPlayerHand()).thenReturn(mock(Hand.class));
        when(mockGame.getDealerHand()).thenReturn(mock(Hand.class));

        when(gameRepository.save(any(Game.class)))
                .thenReturn(Mono.just(mockGame));

    }

    @Test
    @DisplayName("Should create new player if not exists")
    void shouldCreateNewPlayerIfNotExists() {
        CreateGameRequest request = new CreateGameRequest("Maria");
        PlayerName playerName = new PlayerName("Maria");

        when(playerRepository.findByName(any(PlayerName.class)))
                .thenReturn(Mono.empty());

        Player newPlayer = mock(Player.class);
        when(newPlayer.getId()).thenReturn(new PlayerId("player-789"));
        when(newPlayer.getName()).thenReturn(playerName);

        when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(newPlayer));

        Game mockGame = mock(Game.class);
        when(mockGame.getId()).thenReturn(new GameId("game-999"));
        when(mockGame.getPlayerId()).thenReturn(new PlayerId("player-789"));
        when(mockGame.getStatus()).thenReturn(GameStatus.PLAYING);
        when(mockGame.getPlayerHand()).thenReturn(mock(Hand.class));
        when(mockGame.getDealerHand()).thenReturn(mock(Hand.class));

        when(gameRepository.save(any(Game.class)))
                .thenReturn(Mono.just(mockGame));

    }

    @Test
    @DisplayName("Should return game with 2 cards for player and 1 for dealer")
    void shouldReturnGameWithInitialDeal() {
        CreateGameRequest request = new CreateGameRequest("Pedro");

        Player existingPlayer = mock(Player.class);
        when(existingPlayer.getId()).thenReturn(new PlayerId("player-111"));
        when(existingPlayer.getName()).thenReturn(new PlayerName("Pedro"));

        when(playerRepository.findByName(any(PlayerName.class)))
                .thenReturn(Mono.just(existingPlayer));

        Game mockGame = mock(Game.class);
        when(mockGame.getId()).thenReturn(new GameId("game-222"));
        when(mockGame.getPlayerId()).thenReturn(new PlayerId("player-111"));
        when(mockGame.getStatus()).thenReturn(GameStatus.PLAYING);

        Hand playerHand = mock(Hand.class);
        Hand dealerHand = mock(Hand.class);

        when(mockGame.getPlayerHand()).thenReturn(playerHand);
        when(mockGame.getDealerHand()).thenReturn(dealerHand);

        when(gameRepository.save(any(Game.class)))
                .thenReturn(Mono.just(mockGame));

    }
}