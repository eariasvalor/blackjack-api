package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.dto.response.PlayerRankingResponse;
import com.blackjack.application.usecase.game.*;
import com.blackjack.application.usecase.player.DeletePlayerUseCase;
import com.blackjack.application.usecase.player.UpdatePlayerNameUseCase;
import com.blackjack.application.usecase.ranking.GetRankingUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {GameController.class, RankingController.class})
@DisplayName("RankingController - Get Ranking Tests")
class GetRankingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GetRankingUseCase getRankingUseCase;

    @MockBean
    private CreateGameUseCase createGameUseCase;

    @MockBean
    private GetGameByIdUseCase getGameByIdUseCase;

    @MockBean
    private PlayGameUseCase playGameUseCase;

    @MockBean
    private DeleteGameUseCase deleteGameUseCase;

    @MockBean
    private UpdatePlayerNameUseCase updatePlayerNameUseCase;

    @MockBean
    private GetAllGamesUseCase getAllGamesUseCase;

    @MockBean
    private DeletePlayerUseCase deletePlayerUseCase;

    @Test
    @DisplayName("GET /ranking - Should return first page with default parameters")
    void shouldReturnFirstPageWithDefaultParameters() {
        PlayerRankingResponse player1 = new PlayerRankingResponse(
                "player-1", "Alice", 10, 7, 0.70
        );
        PlayerRankingResponse player2 = new PlayerRankingResponse(
                "player-2", "Bob", 5, 5, 1.0
        );

        PageResponse<PlayerRankingResponse> pageResponse = PageResponse.of(
                List.of(player2, player1),
                0,
                10,
                2L
        );

        when(getRankingUseCase.execute(anyInt(), anyInt()))
                .thenReturn(Mono.just(pageResponse));

        webTestClient.get()
                .uri("/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.content[0].playerName").isEqualTo("Bob")
                .jsonPath("$.content[0].winRate").isEqualTo(1.0)
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10)
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.totalPages").isEqualTo(1)
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(true);

        verify(getRankingUseCase).execute(0, 10);
    }

    @Test
    @DisplayName("GET /ranking - Should return second page with custom size")
    void shouldReturnSecondPageWithCustomSize() {
        PlayerRankingResponse player3 = new PlayerRankingResponse(
                "player-3", "Charlie", 8, 2, 0.25
        );

        PageResponse<PlayerRankingResponse> pageResponse = PageResponse.of(
                List.of(player3),
                1,
                2,
                5L
        );

        when(getRankingUseCase.execute(anyInt(), anyInt()))
                .thenReturn(Mono.just(pageResponse));

        webTestClient.get()
                .uri("/ranking?page=1&size=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.page").isEqualTo(1)
                .jsonPath("$.size").isEqualTo(2)
                .jsonPath("$.totalElements").isEqualTo(5)
                .jsonPath("$.totalPages").isEqualTo(3)
                .jsonPath("$.first").isEqualTo(false)
                .jsonPath("$.last").isEqualTo(false);

        verify(getRankingUseCase).execute(1, 2);
    }

    @Test
    @DisplayName("GET /ranking - Should return empty page when no players")
    void shouldReturnEmptyPageWhenNoPlayers() {
        PageResponse<PlayerRankingResponse> emptyPage = PageResponse.of(
                List.of(),
                0,
                10,
                0L
        );

        when(getRankingUseCase.execute(anyInt(), anyInt()))
                .thenReturn(Mono.just(emptyPage));

        webTestClient.get()
                .uri("/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(0)
                .jsonPath("$.totalElements").isEqualTo(0)
                .jsonPath("$.totalPages").isEqualTo(0);

        verify(getRankingUseCase).execute(0, 10);
    }
}