package com.blackjack.application.usecase.ranking;

import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.dto.response.PlayerRankingResponse;
import com.blackjack.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetRankingUseCase {

    private final PlayerRepository playerRepository;

    public Mono<PageResponse<PlayerRankingResponse>> execute(int page, int size) {
        int validatedPage = Math.max(0, page);
        int validatedSize = Math.min(Math.max(1, size), 100);

        log.info("Getting ranking - page: {}, size: {}", validatedPage, validatedSize);

        int offset = validatedPage * validatedSize;

        return playerRepository.findAllByOrderByWinRateDesc(validatedSize, offset)
                .map(player -> new PlayerRankingResponse(
                        player.getId().value(),
                        player.getName().value(),
                        player.getGamesPlayed(),
                        player.getGamesWon(),
                        player.getWinRate() / 100.0
                ))
                .collectList()
                .zipWith(playerRepository.count())
                .map(tuple -> {
                    PageResponse<PlayerRankingResponse> response = PageResponse.of(
                            tuple.getT1(), validatedPage,
                            validatedSize,
                            tuple.getT2());

                    log.info("Ranking retrieved - page: {}/{}, total: {}",
                            validatedPage + 1, response.totalPages(), tuple.getT2());

                    return response;
                })
                .doOnError(error -> log.error("Error getting ranking: {}", error.getMessage()));
    }
}