package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.exception.PlayerNotFoundException;
import com.blackjack.application.usecase.game.*;
import com.blackjack.application.usecase.player.DeletePlayerUseCase;
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

@WebFluxTest(PlayerController.class)
class DeletePlayerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DeletePlayerUseCase deletePlayerUseCase;

    @MockBean
    private UpdatePlayerNameUseCase updatePlayerNameUseCase;

    @Test
    @DisplayName("DELETE /player/{id} - Should return 204 No Content")
    void shouldReturn204WhenDeletedSuccessfully() {
        String playerId = "player-123";

        when(deletePlayerUseCase.execute(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/player/{id}", playerId)
                .exchange()
                .expectStatus().isNoContent();

        verify(deletePlayerUseCase).execute(playerId);
    }

    @Test
    @DisplayName("DELETE /player/{id} - Should return 404 Not Found")
    void shouldReturn404WhenPlayerNotFound() {
        String playerId = "unknown";

        when(deletePlayerUseCase.execute(anyString()))
                .thenReturn(Mono.error(new PlayerNotFoundException("unknown")));

        webTestClient.delete()
                .uri("/player/{id}", playerId)
                .exchange()
                .expectStatus().isNotFound();
    }
}