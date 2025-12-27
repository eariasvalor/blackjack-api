package com.blackjack.application.usecase.player;

import com.blackjack.application.dto.request.UpdatePlayerRequest;
import com.blackjack.application.dto.response.PlayerResponse;
import com.blackjack.application.exception.PlayerNotFoundException;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdatePlayerNameUseCase Tests")
class UpdatePlayerNameUseCaseTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private UpdatePlayerNameUseCase useCase;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = Player.create(new PlayerName("OldName"));
    }

    @Test
    @DisplayName("Should update player name successfully")
    void shouldUpdatePlayerNameSuccessfully() {
        String playerId = testPlayer.getId().value();
        UpdatePlayerRequest request = new UpdatePlayerRequest("NewName");

        when(playerRepository.findById(any(PlayerId.class)))
                .thenReturn(Mono.just(testPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<PlayerResponse> result = useCase.execute(playerId, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.playerId()).isEqualTo(playerId);
                    assertThat(response.playerName()).isEqualTo("NewName");
                })
                .verifyComplete();

        verify(playerRepository).findById(any(PlayerId.class));
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    @DisplayName("Should throw PlayerNotFoundException when player does not exist")
    void shouldThrowPlayerNotFoundExceptionWhenPlayerDoesNotExist() {
        String playerId = "non-existent-player";
        UpdatePlayerRequest request = new UpdatePlayerRequest("NewName");

        when(playerRepository.findById(any(PlayerId.class)))
                .thenReturn(Mono.empty());

        Mono<PlayerResponse> result = useCase.execute(playerId, request);

        StepVerifier.create(result)
                .expectError(PlayerNotFoundException.class)
                .verify();

        verify(playerRepository).findById(any(PlayerId.class));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    @DisplayName("Should reject empty player name")
    void shouldRejectEmptyPlayerName() {
        String playerId = testPlayer.getId().value();
        UpdatePlayerRequest request = new UpdatePlayerRequest("");

        when(playerRepository.findById(any(PlayerId.class)))
                .thenReturn(Mono.just(testPlayer));

        Mono<PlayerResponse> result = useCase.execute(playerId, request);

        StepVerifier.create(result)
                .expectError()
                .verify();
    }
}