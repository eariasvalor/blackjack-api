package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.request.UpdatePlayerRequest;
import com.blackjack.application.dto.response.PlayerResponse;
import com.blackjack.application.usecase.player.UpdatePlayerNameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final UpdatePlayerNameUseCase updatePlayerNameUseCase;

    @PutMapping("/{id}")
    @Operation(
            summary = "Update the player's name",
            description = "Updates the name of an existing player by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Name updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request (empty name)"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<PlayerResponse>> updatePlayerName(
            @Parameter(description = "Player ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody UpdatePlayerRequest request) {

        log.info("PUT /player/{} - Request to update player name to: {}", id, request.playerName());

        return updatePlayerNameUseCase.execute(id, request)
                .map(ResponseEntity::ok)
                .doOnSuccess(response ->
                        log.info("PUT /player/{} - Player name updated successfully to: {}",
                                id, request.playerName())
                );
    }
}