package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.dto.response.PageResponse;
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

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;
@WebFluxTest(GameController.class)
class GetAllGamesControllerTest {

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
    @DisplayName("GET /game - Should return paginated games")
    void shouldReturnPaginatedGames() {
        GameResponse gameResponse = new GameResponse("game-1", "p-1", "Player", List.of(), List.of(), 0, 0, "PLAYING", List.of(), null, null, null
        );;
        PageResponse<GameResponse> pageResponse = PageResponse.of(
                List.of(gameResponse), 0, 10, 1L
        );

        when(getAllGamesUseCase.execute(anyInt(), anyInt()))
                .thenReturn(Mono.just(pageResponse));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/game")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.totalPages").isEqualTo(1);
    }
}