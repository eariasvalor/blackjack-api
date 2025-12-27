package com.blackjack.application.usecase.game;

import com.blackjack.application.exception.GameNotFoundException;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.domain.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteGameUseCase Tests")
class DeleteGameUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private DeleteGameUseCase useCase;

    private Player testPlayer;
    private Game testGame;

    @BeforeEach
    void setUp() {
        testPlayer = Player.create(new PlayerName("TestPlayer"));
        testGame = Game.create(testPlayer.getId());
    }

    @Test
    @DisplayName("Should delete game successfully when game exists")
    void shouldDeleteGameSuccessfully() {
        String gameId = testGame.getId().value();

        when(gameRepository.findById(any(GameId.class)))
                .thenReturn(Mono.just(testGame));
        when(gameRepository.deleteById(any(GameId.class)))
                .thenReturn(Mono.empty());

        Mono<Void> result = useCase.execute(gameId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(gameRepository).findById(any(GameId.class));
        verify(gameRepository).deleteById(any(GameId.class));
    }

    @Test
    @DisplayName("Should throw GameNotFoundException when game does not exist")
    void shouldThrowGameNotFoundExceptionWhenGameDoesNotExist() {
        String gameId = "non-existent-game";

        when(gameRepository.findById(any(GameId.class)))
                .thenReturn(Mono.empty());

        Mono<Void> result = useCase.execute(gameId);

        StepVerifier.create(result)
                .expectError(GameNotFoundException.class)
                .verify();

        verify(gameRepository).findById(any(GameId.class));
        verify(gameRepository, never()).deleteById(any(GameId.class));
    }
}