package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.usecase.game.*;
import com.blackjack.application.usecase.player.DeletePlayerUseCase;
import com.blackjack.application.usecase.player.UpdatePlayerNameUseCase;
import com.blackjack.application.usecase.ranking.GetRankingUseCase;
import com.blackjack.domain.model.valueobject.game.DeckCount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
@DisplayName("GameController - Get Games By Player Tests")
class GetGamesByPlayerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean private CreateGameUseCase createGameUseCase;
    @MockBean private GetGameByIdUseCase getGameByIdUseCase;
    @MockBean private PlayGameUseCase playGameUseCase;
    @MockBean private DeleteGameUseCase deleteGameUseCase;
    @MockBean private GetAllGamesUseCase getAllGamesUseCase;
    @MockBean private GetRankingUseCase getRankingUseCase;
    @MockBean private UpdatePlayerNameUseCase updatePlayerNameUseCase;
    @MockBean private DeletePlayerUseCase deletePlayerUseCase;

    @MockBean
    private GetGamesByPlayerUseCase getGamesByPlayerUseCase;

    @Test
    @DisplayName("GET /game/player/{playerId} - Should return paginated games")
    void shouldReturnPaginatedGamesForPlayer() {
        String playerId = "player-123";
        int page = 0;
        int size = 10;

        GameResponse gameResponse = new GameResponse(
                "game-1", playerId, "TestPlayer",
                List.of(), List.of(), 0, 0, "PLAYING",
                List.of(), LocalDateTime.now(), LocalDateTime.now(), DeckCount.of(1)
        );

        PageResponse<GameResponse> pageResponse = PageResponse.of(
                List.of(gameResponse), page, size, 1
        );

        when(getGamesByPlayerUseCase.execute(anyString(), anyInt(), anyInt()))
                .thenReturn(Mono.just(pageResponse));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/game/player/{playerId}")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build(playerId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[0].gameId").isEqualTo("game-1")
                .jsonPath("$.page").isEqualTo(page)
                .jsonPath("$.size").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(1);
    }
}