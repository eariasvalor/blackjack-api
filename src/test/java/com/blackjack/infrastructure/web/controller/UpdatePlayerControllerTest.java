package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.request.UpdatePlayerRequest;
import com.blackjack.application.dto.response.PlayerResponse;
import com.blackjack.application.exception.PlayerNotFoundException;
import com.blackjack.application.usecase.game.*;
import com.blackjack.application.usecase.player.DeletePlayerUseCase;
import com.blackjack.application.usecase.player.UpdatePlayerNameUseCase;
import com.blackjack.application.usecase.ranking.GetRankingUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(PlayerController.class)
class UpdatePlayerControllerTest {

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
    @DisplayName("PUT /player/{id} - Should update player name successfully")
    void shouldUpdatePlayerNameSuccessfully() {
        String playerId = "player-123";
        UpdatePlayerRequest request = new UpdatePlayerRequest("NewName");

        PlayerResponse response = new PlayerResponse(
                playerId,
                "NewName",
                5,
                3,
                2,
                0,
                0.60,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(updatePlayerNameUseCase.execute(anyString(), any(UpdatePlayerRequest.class)))
                .thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/player/{id}", playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.playerId").isEqualTo(playerId)
                .jsonPath("$.playerName").isEqualTo("NewName")
                .jsonPath("$.gamesPlayed").isEqualTo(5)
                .jsonPath("$.gamesWon").isEqualTo(3)
                .jsonPath("$.winRate").isEqualTo(0.60);

        verify(updatePlayerNameUseCase).execute(playerId, request);
    }

    @Test
    @DisplayName("PUT /player/{id} - Should return 404 when player not found")
    void shouldReturn404WhenPlayerNotFound() {
        String playerId = "non-existent-player";
        UpdatePlayerRequest request = new UpdatePlayerRequest("NewName");

        when(updatePlayerNameUseCase.execute(anyString(), any(UpdatePlayerRequest.class)))
                .thenReturn(Mono.error(new PlayerNotFoundException(playerId)));

        webTestClient.put()
                .uri("/player/{id}", playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("PUT /player/{id} - Should return 400 when player name is missing in body")
    void shouldReturn400WhenPlayerNameIsMissingInBody() {
        String playerId = "player-123";
        String requestBody = "{}";
        webTestClient.put()
                .uri("/player/{id}", playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("PUT /player/{id} - Should return 400 when player name is null")
    void shouldReturn400WhenPlayerNameIsNull() {
        String playerId = "player-123";
        String requestBody = "{\"playerName\": null}";

        webTestClient.put()
                .uri("/player/{id}", playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

}