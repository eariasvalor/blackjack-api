package com.blackjack.application.usecase.ranking;

import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.dto.response.PlayerRankingResponse;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.domain.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetRankingUseCase Tests")
class GetRankingUseCaseTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private GetRankingUseCase useCase;

    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        player1 = Player.create(new PlayerName("Alice"));
        player1.recordWin();
        player1.recordWin();
        player1.recordLoss();

        player2 = Player.create(new PlayerName("Bob"));
        player2.recordWin();

        player3 = Player.create(new PlayerName("Charlie"));
        player3.recordLoss();
    }

    @Test
    @DisplayName("Should return first page of ranking with default parameters")
    void shouldReturnFirstPageOfRanking() {
        int page = 0;
        int size = 10;

        when(playerRepository.findAllByOrderByWinRateDesc(anyInt(), anyInt()))
                .thenReturn(Flux.just(player2, player1, player3));
        when(playerRepository.count())
                .thenReturn(Mono.just(3L));

        Mono<PageResponse<PlayerRankingResponse>> result = useCase.execute(page, size);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.content()).hasSize(3);
                    assertThat(response.page()).isEqualTo(0);
                    assertThat(response.size()).isEqualTo(10);
                    assertThat(response.totalElements()).isEqualTo(3L);
                    assertThat(response.totalPages()).isEqualTo(1);
                    assertThat(response.first()).isTrue();
                    assertThat(response.last()).isTrue();

                    assertThat(response.content().get(0).playerName()).isEqualTo("Bob");
                    assertThat(response.content().get(0).winRate()).isEqualTo(1.0);

                    assertThat(response.content().get(1).playerName()).isEqualTo("Alice");
                    assertThat(response.content().get(1).gamesPlayed()).isEqualTo(3);
                    assertThat(response.content().get(1).gamesWon()).isEqualTo(2);

                    assertThat(response.content().get(2).playerName()).isEqualTo("Charlie");
                    assertThat(response.content().get(2).winRate()).isEqualTo(0.0);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return second page of ranking")
    void shouldReturnSecondPage() {
        int page = 1;
        int size = 2;

        when(playerRepository.findAllByOrderByWinRateDesc(anyInt(), anyInt()))
                .thenReturn(Flux.just(player3));
        when(playerRepository.count())
                .thenReturn(Mono.just(3L));

        Mono<PageResponse<PlayerRankingResponse>> result = useCase.execute(page, size);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.content()).hasSize(1);
                    assertThat(response.page()).isEqualTo(1);
                    assertThat(response.size()).isEqualTo(2);
                    assertThat(response.totalElements()).isEqualTo(3L);
                    assertThat(response.totalPages()).isEqualTo(2);
                    assertThat(response.first()).isFalse();
                    assertThat(response.last()).isTrue();

                    assertThat(response.content().get(0).playerName()).isEqualTo("Charlie");
                })
                .verifyComplete();

        verify(playerRepository).findAllByOrderByWinRateDesc(2, 2);
    }

    @Test
    @DisplayName("Should return empty page when no players exist")
    void shouldReturnEmptyPageWhenNoPlayers() {
        int page = 0;
        int size = 10;

        when(playerRepository.findAllByOrderByWinRateDesc(anyInt(), anyInt()))
                .thenReturn(Flux.empty());

        when(playerRepository.count())
                .thenReturn(Mono.just(0L));

        Mono<PageResponse<PlayerRankingResponse>> result = useCase.execute(page, size);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.content()).isEmpty();
                    assertThat(response.page()).isEqualTo(0);
                    assertThat(response.size()).isEqualTo(10);
                    assertThat(response.totalElements()).isEqualTo(0L);
                    assertThat(response.totalPages()).isEqualTo(0);
                    assertThat(response.first()).isTrue();
                    assertThat(response.last()).isTrue();
                })
                .verifyComplete();
    }
}