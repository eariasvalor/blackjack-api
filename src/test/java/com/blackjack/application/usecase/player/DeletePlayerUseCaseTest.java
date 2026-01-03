package com.blackjack.application.usecase.player;

import com.blackjack.application.exception.PlayerNotFoundException;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
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
class DeletePlayerUseCaseTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private DeletePlayerUseCase useCase;

    @Test
    @DisplayName("Should delete player and their games successfully")
    void shouldDeletePlayerAndGamesSuccessfully() {
        String playerId = "player-123";

        when(playerRepository.existsById(any(PlayerId.class)))
                .thenReturn(Mono.just(true));

        when(gameRepository.deleteByPlayerId(any(PlayerId.class)))
                .thenReturn(Mono.empty());
        when(playerRepository.deleteById(any(PlayerId.class)))
                .thenReturn(Mono.empty());
        Mono<Void> result = useCase.execute(playerId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(gameRepository).deleteByPlayerId(any(PlayerId.class));
        verify(playerRepository).deleteById(any(PlayerId.class));
    }

    @Test
    @DisplayName("Should throw exception when player does not exist")
    void shouldThrowExceptionWhenPlayerDoesNotExist() {
        String playerId = "non-existent";

        when(playerRepository.existsById(any(PlayerId.class)))
                .thenReturn(Mono.just(false));

        Mono<Void> result = useCase.execute(playerId);

        StepVerifier.create(result)
                .expectError(PlayerNotFoundException.class)
                .verify();

        verify(gameRepository, never()).deleteByPlayerId(any(PlayerId.class));
        verify(playerRepository, never()).deleteById(any(PlayerId.class));
    }
}