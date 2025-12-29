package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.exception.GameNotFoundException;
import com.blackjack.application.usecase.game.CreateGameUseCase;
import com.blackjack.application.usecase.game.DeleteGameUseCase;
import com.blackjack.application.usecase.game.GetGameByIdUseCase;
import com.blackjack.application.usecase.game.PlayGameUseCase;
import com.blackjack.application.usecase.player.UpdatePlayerNameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
class DeleteGameControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DeleteGameUseCase deleteGameUseCase;

    @MockBean
    private CreateGameUseCase createGameUseCase;

    @MockBean
    private GetGameByIdUseCase getGameByIdUseCase;

    @MockBean
    private PlayGameUseCase playGameUseCase;

    @MockBean
    private UpdatePlayerNameUseCase updatePlayerNameUseCase;

    @Test
    @DisplayName("DELETE /game/{id}/delete - Should delete game successfully")
    void shouldDeleteGameSuccessfully() {
        String gameId = "game-123";

        when(deleteGameUseCase.execute(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/game/{id}/delete", gameId)
                .exchange()
                .expectStatus().isNoContent();

        verify(deleteGameUseCase).execute(gameId);
    }

    @Test
    @DisplayName("DELETE /game/{id}/delete - Should return 404 when game not found")
    void shouldReturn404WhenGameNotFound() {
        String gameId = "non-existent-game";

        when(deleteGameUseCase.execute(anyString()))
                .thenReturn(Mono.error(new GameNotFoundException(gameId)));

        webTestClient.delete()
                .uri("/game/{id}/delete", gameId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }
}