package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.exception.GameNotFoundException;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
@DisplayName("GameController - Get Game By Id Tests")
class GetGameByIdControllerTest {

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

    @MockBean
    private GetGamesByPlayerUseCase getGamesByPlayerUseCase;

    @Test
    @DisplayName("GET /game/{id} - Should return 200 OK when game exists")
    void shouldReturn200WhenGameExists() {
        String gameId = "game-123";
        GameResponse gameResponse = new GameResponse(
                gameId,
                "player-456",
                "TestPlayer",
                Collections.emptyList(),
                Collections.emptyList(),
                15,
                10,
                "PLAYING",
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                DeckCount.of(1)
        );

        when(getGameByIdUseCase.execute(anyString()))
                .thenReturn(Mono.just(gameResponse));

        webTestClient.get()
                .uri("/game/{id}", gameId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo(gameId)
                .jsonPath("$.playerName").isEqualTo("TestPlayer")
                .jsonPath("$.status").isEqualTo("PLAYING");
    }

    @Test
    @DisplayName("GET /game/{id} - Should return 404 when game does not exist")
    void shouldReturn404WhenGameDoesNotExist() {
        String gameId = "non-existent-game";

        when(getGameByIdUseCase.execute(anyString()))
                .thenReturn(Mono.error(new GameNotFoundException(gameId)));

        webTestClient.get()
                .uri("/game/{id}", gameId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").exists();
    }
}