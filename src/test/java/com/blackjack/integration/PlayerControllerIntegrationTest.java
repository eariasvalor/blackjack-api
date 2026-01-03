package com.blackjack.integration;

import com.blackjack.application.dto.request.CreateGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.config.TestcontainersConfiguration;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class PlayerControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameRepository gameRepository;

    @Test
    @DisplayName("Should delete player and associated games from databases")
    void shouldDeletePlayerAndGames() {
        String playerName = "PlayerToDelete_" + System.currentTimeMillis();
        GameResponse gameResponse = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateGameRequest(playerName, 1))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameResponse.class)
                .returnResult().getResponseBody();

        String playerId = gameResponse.playerId();
        String gameId = gameResponse.gameId();

        StepVerifier.create(playerRepository.existsById(PlayerId.from(playerId)))
                .expectNext(true).verifyComplete();
        StepVerifier.create(gameRepository.existsById(GameId.from(gameId)))
                .expectNext(true).verifyComplete();

        webTestClient.delete()
                .uri("/player/{id}", playerId)
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier.create(playerRepository.existsById(PlayerId.from(playerId)))
                .expectNext(false).verifyComplete();

        StepVerifier.create(gameRepository.findByPlayerId(PlayerId.from(playerId)))
                .expectNextCount(0).verifyComplete();
    }
}