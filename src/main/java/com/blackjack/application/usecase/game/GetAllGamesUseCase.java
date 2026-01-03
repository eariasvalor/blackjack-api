package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import com.blackjack.application.dto.response.PageResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllGamesUseCase {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameResponseMapper mapper;

    public Mono<PageResponse<GameResponse>> execute(int page, int size) {
        log.info("Request to retrieve games page {} with size {}", page, size);

        Mono<List<GameResponse>> gamesListMono = gameRepository.findAll(page, size)
                .flatMap(game -> playerRepository.findById(game.getPlayerId())
                        .map(player -> mapper.toResponse(
                                game,
                                player,
                                game.getDeck().getDeckCount()
                        ))
                )
                .collectList();

        Mono<Long> totalElementsMono = gameRepository.count();

        return Mono.zip(gamesListMono, totalElementsMono)
                .map(tuple -> PageResponse.of(
                        tuple.getT1(),
                        page,
                        size,
                        tuple.getT2()));
    }
}