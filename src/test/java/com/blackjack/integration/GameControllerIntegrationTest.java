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
        CreateGameRequest request = new CreateGameRequest(uniquePlayerName);

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
        assertThat(response.playerHandValue()).isBetween(2, 21);
        assertThat(response.dealerVisibleValue()).isBetween(1, 11);

        Game savedGame = gameRepository.findById(GameId.from(response.gameId()))
                .block();

        assertThat(savedGame).isNotNull();
        assertThat(savedGame.getStatus()).isEqualTo(GameStatus.PLAYING);

        Player savedPlayer = playerRepository.findById(PlayerId.from(response.playerId()))
                .block();

        assertThat(savedPlayer).isNotNull();
        assertThat(savedPlayer.getName().value()).isEqualTo(uniquePlayerName);
        assertThat(savedPlayer.getGamesPlayed()).isEqualTo(0);
        assertThat(savedPlayer.getWinRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should reuse existing player when creating new game")
    void shouldReuseExistingPlayerWhenCreatingNewGame() {
        String uniquePlayerName = "Maria_" + System.currentTimeMillis();
        CreateGameRequest request = new CreateGameRequest(uniquePlayerName);

        GameResponse firstGame = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameResponse.class)
                .returnResult()
                .getResponseBody();

        String firstPlayerId = firstGame.playerId();
        String firstGameId = firstGame.gameId();

        GameResponse secondGame = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(secondGame.playerId()).isEqualTo(firstPlayerId);
        assertThat(secondGame.gameId()).isNotEqualTo(firstGameId);
        assertThat(secondGame.playerName()).isEqualTo(uniquePlayerName);

        Game firstGameFromDb = gameRepository.findById(GameId.from(firstGameId)).block();
        Game secondGameFromDb = gameRepository.findById(GameId.from(secondGame.gameId())).block();

        assertThat(firstGameFromDb).isNotNull();
        assertThat(secondGameFromDb).isNotNull();
        assertThat(firstGameFromDb.getId()).isNotEqualTo(secondGameFromDb.getId());

        Player playerFromDb = playerRepository.findById(PlayerId.from(firstPlayerId)).block();
        assertThat(playerFromDb).isNotNull();
    }

    @Test
    @DisplayName("Should create different players for different names")
    void shouldCreateDifferentPlayersForDifferentNames() {
        String estherName = "Esther_" + System.currentTimeMillis();
        String mariaName = "Maria_" + System.currentTimeMillis();

        CreateGameRequest estherRequest = new CreateGameRequest(estherName);
        CreateGameRequest mariaRequest = new CreateGameRequest(mariaName);

        GameResponse estherGame = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(estherRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameResponse.class)
                .returnResult()
                .getResponseBody();

        GameResponse mariaGame = webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mariaRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(mariaGame.playerId()).isNotEqualTo(estherGame.playerId());
        assertThat(mariaGame.gameId()).isNotEqualTo(estherGame.gameId());
        assertThat(mariaGame.playerName()).isEqualTo(mariaName);
        assertThat(estherGame.playerName()).isEqualTo(estherName);

        Player estherFromDb = playerRepository.findById(PlayerId.from(estherGame.playerId())).block();
        Player mariaFromDb = playerRepository.findById(PlayerId.from(mariaGame.playerId())).block();

        assertThat(estherFromDb).isNotNull();
        assertThat(mariaFromDb).isNotNull();
        assertThat(estherFromDb.getName().value()).isEqualTo(estherName);
        assertThat(mariaFromDb.getName().value()).isEqualTo(mariaName);
    }

    @Test
    @DisplayName("Should return 400 for invalid player name")
    void shouldReturn400ForInvalidPlayerName() {
        CreateGameRequest emptyNameRequest = new CreateGameRequest("");

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emptyNameRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should return 400 for null player name")
    void shouldReturn400ForNullPlayerName() {
        CreateGameRequest nullNameRequest = new CreateGameRequest(null);

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(nullNameRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }
}