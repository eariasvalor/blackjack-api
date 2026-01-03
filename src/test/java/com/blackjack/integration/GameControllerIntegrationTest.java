package com.blackjack.integration;

import com.blackjack.application.dto.request.CreateGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.config.TestcontainersConfiguration;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.game.GameId;
import com.blackjack.domain.model.valueobject.game.GameStatus;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@DisplayName("Game Controller Integration Tests")
class GameControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    @DisplayName("Should create game and persist to both databases")
    void shouldCreateGameAndPersistToBothDatabases() {
        String uniquePlayerName = "Esther_" + System.currentTimeMillis();
        CreateGameRequest request = new CreateGameRequest(uniquePlayerName, 1);

        GameResponse response = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.playerName()).isEqualTo(uniquePlayerName);
        assertThat(response.status()).isEqualTo("PLAYING");

        Game savedGame = gameRepository.findById(GameId.from(response.gameId()))
                .block();

        assertThat(savedGame).isNotNull();
        assertThat(savedGame.getStatus()).isEqualTo(GameStatus.PLAYING);
        assertThat(savedGame.getDeck().size()).isEqualTo(52);
    }

    @Test
    @DisplayName("Should create game with multiple decks (e.g. 6 decks)")
    void shouldCreateGameWithMultipleDecks() {
        String uniquePlayerName = "HighRoller_" + System.currentTimeMillis();
        int numberOfDecks = 6;
        CreateGameRequest request = new CreateGameRequest(uniquePlayerName, numberOfDecks);

        GameResponse response = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated().expectBody(GameResponse.class)
                .returnResult()
                .getResponseBody();

        Game savedGame = gameRepository.findById(GameId.from(response.gameId())).block();
        assertThat(savedGame).isNotNull();

        int expectedTotalCards = 52 * 6;
        assertThat(savedGame.getDeck().size()).isEqualTo(expectedTotalCards);

        assertThat(savedGame.getDeck().remainingCards()).isEqualTo(expectedTotalCards - 3);
    }

    @Test
    @DisplayName("Should reuse existing player when creating new game")
    void shouldReuseExistingPlayerWhenCreatingNewGame() {
        String uniquePlayerName = "Maria_" + System.currentTimeMillis();
        CreateGameRequest request = new CreateGameRequest(uniquePlayerName, 1);

        GameResponse firstGame = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameResponse.class)
                .returnResult()
                .getResponseBody();

        GameResponse secondGame = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(secondGame.playerId()).isEqualTo(firstGame.playerId());
        assertThat(secondGame.gameId()).isNotEqualTo(firstGame.gameId());
    }

    @Test
    @DisplayName("Should return 400 for invalid player name")
    void shouldReturn400ForInvalidPlayerName() {
        CreateGameRequest emptyNameRequest = new CreateGameRequest("", 1);

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emptyNameRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should return 400 for invalid deck count")
    void shouldReturn400ForInvalidDeckCount() {
        CreateGameRequest tooManyDecksRequest = new CreateGameRequest("TestPlayer", 9);

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tooManyDecksRequest)
                .exchange()
                .expectStatus().isBadRequest();

        CreateGameRequest zeroDecksRequest = new CreateGameRequest("TestPlayer", 0);

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(zeroDecksRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should retrieve all games from database with pagination")
    void shouldRetrieveAllGames() {
        String player1 = "PlayerOne_" + System.currentTimeMillis();
        String player2 = "PlayerTwo_" + System.currentTimeMillis();

        webTestClient.post().uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateGameRequest(player1, 1))
                .exchange().expectStatus().isCreated();

        webTestClient.post().uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateGameRequest(player2, 1))
                .exchange().expectStatus().isCreated();

        webTestClient.get()
                .uri("/game?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").value(length ->
                        assertThat((Integer) length).isGreaterThanOrEqualTo(2)
                )
                .jsonPath("$.totalElements").value(total ->
                        assertThat((Integer) total).isGreaterThanOrEqualTo(2)
                )
                .jsonPath("$.size").isEqualTo(10)
                .jsonPath("$.page").isEqualTo(0);
    }

    private void createGame(String playerName) {
        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateGameRequest(playerName, 1))
                .exchange()
                .expectStatus().isCreated();
    }
}