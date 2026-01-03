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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGameUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameResponseMapper mapper;

    @InjectMocks
    private CreateGameUseCase useCase;

    private Player testPlayer;
    private Game testGame;
    private GameResponse testResponse;
    private CreateGameRequest request;

    @BeforeEach
    void setUp() {
        testPlayer = Player.create(new PlayerName("TestPlayer"));
        testGame = Game.create(testPlayer.getId());

        testResponse = new GameResponse(
                testGame.getId().value(),
                testPlayer.getId().value(),
                testPlayer.getName().value(),
                Collections.emptyList(),
                Collections.emptyList(),
                0,
                0,
                "PLAYING",
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                testGame.getDeck().getDeckCount()
        );

        request = new CreateGameRequest("TestPlayer", 1);
    }

    @Test
    @DisplayName("Should create game with existing player")
    void shouldCreateGameWithExistingPlayer() {
        when(playerRepository.findByName(any(PlayerName.class)))
                .thenReturn(Mono.just(testPlayer));
        when(gameRepository.save(any(Game.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mapper.toResponse(any(Game.class), any(Player.class), any(DeckCount.class)))
                .thenReturn(testResponse);

        Mono<GameResponse> result = useCase.execute(request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.playerName()).isEqualTo("TestPlayer");
                })
                .verifyComplete();

        verify(playerRepository).findByName(any(PlayerName.class));
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("Should create new player if doesn't exist")
    void shouldCreateNewPlayerIfNotExists() {
        CreateGameRequest newPlayerRequest = new CreateGameRequest("NewPlayer", 1);

        when(playerRepository.findByName(any(PlayerName.class)))
                .thenReturn(Mono.empty());
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(gameRepository.save(any(Game.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(mapper.toResponse(any(Game.class), any(Player.class), any(DeckCount.class)))
                .thenReturn(testResponse);

        Mono<GameResponse> result = useCase.execute(newPlayerRequest);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(playerRepository).save(any(Player.class));
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("Should create game with configured decks (Multi-Deck)")
    void shouldCreateGameWithConfiguredDecks() {
        CreateGameRequest multiDeckRequest = new CreateGameRequest("ProPlayer", 6);
        Player proPlayer = Player.create(new PlayerName("ProPlayer"));

        when(playerRepository.findByName(any(PlayerName.class)))
                .thenReturn(Mono.just(proPlayer));

        when(gameRepository.save(any(Game.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(mapper.toResponse(any(Game.class), any(Player.class), any(DeckCount.class)))
                .thenReturn(testResponse);

        Mono<GameResponse> result = useCase.execute(multiDeckRequest);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(gameRepository).save(argThat(game ->
                game.getDeck().size() == 312));
    }
}