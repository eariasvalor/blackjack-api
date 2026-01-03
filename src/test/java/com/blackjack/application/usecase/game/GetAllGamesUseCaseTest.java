package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.aggregate.mother.GameMother;
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
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllGamesUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameResponseMapper mapper;

    @InjectMocks
    private GetAllGamesUseCase useCase;

    @Test
    @DisplayName("Should return paginated games")
    void shouldReturnPaginatedGames() {
                int page = 0;
        int size = 10;
        Game game = GameMother.defaultGame();
        Player player = Player.create(new PlayerName("TestPlayer"));
                GameResponse gameResponse = mock(GameResponse.class);

                when(gameRepository.findAll(anyInt(), anyInt())).thenReturn(Flux.just(game));
        when(gameRepository.count()).thenReturn(Mono.just(1L));
        when(playerRepository.findById(any(PlayerId.class))).thenReturn(Mono.just(player));
        when(mapper.toResponse(any(Game.class), any(Player.class), any(DeckCount.class)))
                .thenReturn(gameResponse);

                Mono<PageResponse<GameResponse>> result = useCase.execute(page, size);

                StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.content()).hasSize(1);
                    assertThat(response.content().get(0)).isEqualTo(gameResponse);
                    assertThat(response.totalElements()).isEqualTo(1L);
                    assertThat(response.totalPages()).isEqualTo(1);
                    assertThat(response.page()).isEqualTo(page);
                    assertThat(response.size()).isEqualTo(size);
                })
                .verifyComplete();

                verify(gameRepository).findAll(page, size);
        verify(gameRepository).count();
    }
}