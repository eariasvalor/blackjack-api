package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.exception.PlayerNotFoundException;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.game.DeckCount;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetGamesByPlayerUseCase Tests")
public class GetGamesByPlayerUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameResponseMapper mapper;

    @InjectMocks
    private GetGamesByPlayerUseCase useCase;

    @Test
    @DisplayName("Should return paginated games when player exists")
    void shouldReturnPaginatedGamesWhenPlayerExists() {
        String playerIdStr = "player-123";
        PlayerId playerId = PlayerId.from(playerIdStr);
        Player player = Player.create(new PlayerName("TestPlayer"));
        Game game = Game.create(playerId);

        int page = 0;
        int size = 10;

        GameResponse gameResponse = new GameResponse(
                "game-1", playerIdStr, "TestPlayer",
                List.of(), List.of(), 0, 0, "PLAYING",
                List.of(), LocalDateTime.now(), LocalDateTime.now(), DeckCount.of(1)
        );

        Mockito.when(playerRepository.findById(any(PlayerId.class))).thenReturn(Mono.just(player));

        Mockito.when(gameRepository.findByPlayerId(any(PlayerId.class), anyInt(), anyInt()))
                .thenReturn(Flux.just(game));
        Mockito.when(gameRepository.countByPlayerId(any(PlayerId.class)))
                .thenReturn(Mono.just(1L));

        Mockito.when(mapper.toResponse(any(), any(), any())).thenReturn(gameResponse);

        Mono<PageResponse<GameResponse>> result = useCase.execute(playerIdStr, page, size);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.content().size() == 1 &&
                                response.totalElements() == 1 &&
                                response.page() == 0
                )
                .verifyComplete();

        verify(gameRepository).findByPlayerId(playerId, page, size);
        verify(gameRepository).countByPlayerId(playerId);
    }

    @Test
    @DisplayName("Should throw exception when player does not exist")
    void shouldThrowExceptionWhenPlayerDoesNotExist() {
        String playerIdStr = "unknown-player";

        when(playerRepository.findById(any(PlayerId.class))).thenReturn(Mono.empty());

        Mono<PageResponse<GameResponse>> result = useCase.execute(playerIdStr, 0, 10);

        StepVerifier.create(result)
                .expectError(PlayerNotFoundException.class)
                .verify();

        verify(gameRepository, never()).findByPlayerId(any(PlayerId.class), anyInt(), anyInt());
    }
}
