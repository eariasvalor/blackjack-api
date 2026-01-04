package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.request.PlayGameRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
class PlayGameControllerTest {

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
    @DisplayName("POST /game/{id}/play - Should execute HIT action successfully")
    void shouldExecuteHitActionSuccessfully() {
        String gameId = "game-123";
        PlayGameRequest request = new PlayGameRequest("HIT");

        GameResponse response = new GameResponse(
                gameId,
                "player-456",
                "TestPlayer",
                Collections.emptyList(),
                Collections.emptyList(),
                20,
                10,
                "PLAYING",
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                DeckCount.of(1)
        );

        when(playGameUseCase.execute(anyString(), any(PlayGameRequest.class)))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/game/{id}/play", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo(gameId)
                .jsonPath("$.playerName").isEqualTo("TestPlayer")
                .jsonPath("$.status").isEqualTo("PLAYING");
    }

    @Test
    @DisplayName("POST /game/{id}/play - Should execute STAND action successfully")
    void shouldExecuteStandActionSuccessfully() {
        String gameId = "game-123";
        PlayGameRequest request = new PlayGameRequest("STAND");

        GameResponse response = new GameResponse(
                gameId,
                "player-456",
                "TestPlayer",
                Collections.emptyList(),
                Collections.emptyList(),
                19,
                20,
                "DEALER_WINS",
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                DeckCount.of(1)
        );

        when(playGameUseCase.execute(anyString(), any(PlayGameRequest.class)))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/game/{id}/play", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo(gameId)
                .jsonPath("$.status").isEqualTo("DEALER_WINS");
    }

    @Test
    @DisplayName("POST /game/{id}/play - Should return 404 when game not found")
    void shouldReturn404WhenGameNotFound() {
        String gameId = "non-existent-game";
        PlayGameRequest request = new PlayGameRequest("HIT");

        when(playGameUseCase.execute(anyString(), any(PlayGameRequest.class)))
                .thenReturn(Mono.error(new GameNotFoundException(gameId)));

        webTestClient.post()
                .uri("/game/{id}/play", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("POST /game/{id}/play - Should return 400 when action is invalid")
    void shouldReturn400WhenActionIsInvalid() {
        String gameId = "game-123";
        PlayGameRequest request = new PlayGameRequest("INVALID_ACTION");

        when(playGameUseCase.execute(anyString(), any(PlayGameRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid action")));

        webTestClient.post()
                .uri("/game/{id}/play", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("POST /game/{id}/play - Should return 400 when action is empty")
    void shouldReturn400WhenActionIsEmpty() {
        String gameId = "game-123";
        PlayGameRequest request = new PlayGameRequest("");

        webTestClient.post()
                .uri("/game/{id}/play", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}