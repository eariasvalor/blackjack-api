package com.blackjack.infrastructure.web.exception;

import com.blackjack.application.exception.GameNotFoundException;
import com.blackjack.application.usecase.game.CreateGameUseCase;
import com.blackjack.application.usecase.game.DeleteGameUseCase;
import com.blackjack.application.usecase.game.GetGameByIdUseCase;
import com.blackjack.application.usecase.game.PlayGameUseCase;
import com.blackjack.infrastructure.web.controller.GameController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GetGameByIdUseCase getGameByIdUseCase;

    @MockBean
    private CreateGameUseCase createGameUseCase;

    @MockBean
    private PlayGameUseCase playGameUseCase;

    @MockBean
    private DeleteGameUseCase deleteGameUseCase;

    @Test
    @DisplayName("Should handle GameNotFoundException and return 404")
    void shouldHandleGameNotFoundExceptionAndReturn404() {
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
                .jsonPath("$.message").value(msg -> msg.toString().contains(gameId));
    }
}