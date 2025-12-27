package com.blackjack.infrastructure.web.controller;

import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.dto.response.PlayerRankingResponse;
import com.blackjack.application.usecase.ranking.GetRankingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final GetRankingUseCase getRankingUseCase;

    @GetMapping
    @Operation(
            summary = "Get player ranking",
            description = "Returns the players ranking ordered by win rate"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<PageResponse<PlayerRankingResponse>>> getRanking(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /ranking - Request to get ranking (page: {}, size: {})", page, size);

        return getRankingUseCase.execute(page, size)
                .map(ResponseEntity::ok)
                .doOnSuccess(response ->
                        log.info("GET /ranking - Ranking retrieved successfully (page: {}, totalElements: {})",
                                page, response.getBody().totalElements())
                );
    }
}