package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.request.CreateGameRequest;
import com.blackjack.application.dto.response.GameResponse;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
@DisplayName("GameController - Create Game Tests")
class GameControllerTest {

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
    @DisplayName("POST /game/new - Should create a new game and return 201 Created")
    void shouldCreateNewGame() {
        CreateGameRequest request = new CreateGameRequest("John", 1);

        GameResponse expectedResponse = new GameResponse(
                "game-123",
                "player-456",
                "John",
                List.of("7♠", "9♥"),
                List.of("K♦"),
                16,
                10,
                "PLAYING",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                DeckCount.of(1)
        );

        when(createGameUseCase.execute(any(CreateGameRequest.class)))
                .thenReturn(Mono.just(expectedResponse));

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()

                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo("game-123")
                .jsonPath("$.playerId").isEqualTo("player-456")
                .jsonPath("$.playerName").isEqualTo("John")
                .jsonPath("$.playerHand[0]").isEqualTo("7♠")
                .jsonPath("$.playerHand[1]").isEqualTo("9♥")
                .jsonPath("$.dealerVisibleCards[0]").isEqualTo("K♦")
                .jsonPath("$.playerHandValue").isEqualTo(16)
                .jsonPath("$.dealerVisibleValue").isEqualTo(10)
                .jsonPath("$.status").isEqualTo("PLAYING");

        verify(createGameUseCase).execute(any(CreateGameRequest.class));
    }

    @Test
    @DisplayName("POST /game/new - Should return 400 Bad Request when player name is empty")
    void shouldReturnBadRequestWhenPlayerNameIsEmpty() {
        CreateGameRequest request = new CreateGameRequest("", 1);

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /game/new - Should return 400 Bad Request when player name is null")
    void shouldReturnBadRequestWhenPlayerNameIsNull() {
        String requestBody = "{}";

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

}
