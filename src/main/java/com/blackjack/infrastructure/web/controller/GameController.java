package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.request.CreateGameRequest;
import com.blackjack.application.dto.request.PlayGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.application.usecase.game.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final CreateGameUseCase createGameUseCase;
    private final GetGameByIdUseCase getGameByIdUseCase;
    private final PlayGameUseCase playGameUseCase;
    private final DeleteGameUseCase deleteGameUseCase;
    private final GetAllGamesUseCase getAllGamesUseCase;
    private final GetGamesByPlayerUseCase getGamesByPlayerUseCase;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GameResponse> createGame(@RequestBody @Valid CreateGameRequest request) {
        return createGameUseCase.execute(request);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GameResponse>> getGameById(@PathVariable String id) {
        return getGameByIdUseCase.execute(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{id}/play")
    @Operation(
            summary = "Play",
            description = "Execute an action (HIT or STAND) in an existing Blackjack game."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Play performed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid action or game already finished"),
            @ApiResponse(responseCode = "404", description = "Game not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<GameResponse>> playGame(
            @Parameter(description = "Game ID", required = true)
            @PathVariable String id,
            @Parameter(description = "Action to execute (HIT or STAND)", required = true)
            @Valid @RequestBody PlayGameRequest request) {

        log.info("POST /game/{}/play - Request to execute action: {}", id, request.action());

        return playGameUseCase.execute(id, request)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("POST /game/{}/play - Action {} executed. Game status: {}",
                        id, request.action(), response.getBody().status()));
    }

    @DeleteMapping("/{id}/delete")
    @Operation(
            summary = "Delete game",
            description = "Deletes an existing Blackjack game by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Game successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Game not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<Void>> deleteGame(@PathVariable String id) {
        return deleteGameUseCase.execute(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .doOnSuccess(response -> log.info("DELETE /game/{}/delete - Game deleted successfully", id));
    }

    @GetMapping
    @Operation(summary = "Get all games", description = "Retrieves a paginated list of games")
    public Mono<ResponseEntity<PageResponse<GameResponse>>> getAllGames(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /game - Request to get games page: {}, size: {}", page, size);

        return getAllGamesUseCase.execute(page, size)
                .map(ResponseEntity::ok)
                .doOnSuccess(r -> log.info("GET /game - Games page retrieved successfully"));
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get games by player", description = "Retrieves paginated games for a specific player")
    public Mono<ResponseEntity<PageResponse<GameResponse>>> getGamesByPlayer(
            @PathVariable String playerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /game/player/{} - Request games page {}, size {}", playerId, page, size);

        return getGamesByPlayerUseCase.execute(playerId, page, size)
                .map(ResponseEntity::ok);
    }
}
