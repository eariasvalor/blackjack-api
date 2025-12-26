package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.request.PlayGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.exception.GameNotFoundException;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.player.PlayerId;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayGameUseCase Tests")
class PlayGameUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameResponseMapper mapper;

    @InjectMocks
    private PlayGameUseCase useCase;

    private Player testPlayer;
    private Game testGame;
    private GameResponse testResponse;

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
                15,
                10,
                "PLAYING",
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should execute HIT action successfully")
    void shouldExecuteHitActionSuccessfully() {
        String gameId = testGame.getId().value();
        PlayGameRequest request = new PlayGameRequest("HIT");

        when(gameRepository.findById(any(GameId.class)))
                .thenReturn(Mono.just(testGame));
        when(gameRepository.save(any(Game.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(playerRepository.findById(any(PlayerId.class)))
                .thenReturn(Mono.just(testPlayer));
        when(mapper.toResponse(any(Game.class), any(Player.class)))
                .thenReturn(testResponse);

        Mono<GameResponse> result = useCase.execute(gameId, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.gameId()).isEqualTo(gameId);
                })
                .verifyComplete();

        verify(gameRepository).findById(any(GameId.class));
        verify(gameRepository).save(any(Game.class));
        verify(playerRepository).findById(any(PlayerId.class));
        verify(mapper).toResponse(any(Game.class), any(Player.class));
    }

    @Test
    @DisplayName("Should execute STAND action successfully")
    void shouldExecuteStandActionSuccessfully() {
        String gameId = testGame.getId().value();
        PlayGameRequest request = new PlayGameRequest("STAND");

        when(gameRepository.findById(any(GameId.class)))
                .thenReturn(Mono.just(testGame));
        when(gameRepository.save(any(Game.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(playerRepository.findById(any(PlayerId.class)))
                .thenReturn(Mono.just(testPlayer));
        when(mapper.toResponse(any(Game.class), any(Player.class)))
                .thenReturn(testResponse);

        Mono<GameResponse> result = useCase.execute(gameId, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.gameId()).isEqualTo(gameId);
                })
                .verifyComplete();

        verify(gameRepository).findById(any(GameId.class));
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("Should throw exception when action is invalid")
    void shouldThrowExceptionWhenActionIsInvalid() {
        String gameId = testGame.getId().value();
        PlayGameRequest request = new PlayGameRequest("INVALID_ACTION");

        when(gameRepository.findById(any(GameId.class)))
                .thenReturn(Mono.just(testGame));

        Mono<GameResponse> result = useCase.execute(gameId, request);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(gameRepository).findById(any(GameId.class));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Should throw GameNotFoundException when game does not exist")
    void shouldThrowGameNotFoundExceptionWhenGameDoesNotExist() {
        String gameId = "non-existent-game";
        PlayGameRequest request = new PlayGameRequest("HIT");

        when(gameRepository.findById(any(GameId.class)))
                .thenReturn(Mono.empty());

        Mono<GameResponse> result = useCase.execute(gameId, request);

        StepVerifier.create(result)
                .expectError(GameNotFoundException.class)
                .verify();

        verify(gameRepository).findById(any(GameId.class));
        verify(gameRepository, never()).save(any(Game.class));
    }
}
