package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.dto.response.PageResponse;
import com.blackjack.application.exception.PlayerNotFoundException;
import com.blackjack.application.mapper.GameResponseMapper;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetGamesByPlayerUseCase {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameResponseMapper mapper;

    public Mono<PageResponse<GameResponse>> execute(String playerId, int page, int size) {
        log.info("Request to get games for player: {} [page: {}, size: {}]", playerId, page, size);
        PlayerId id = PlayerId.from(playerId);

        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .flatMap(player ->
                        Mono.zip(
                                        gameRepository.findByPlayerId(id, page, size)
                                                .map(game -> mapper.toResponse(game, player, game.getDeck().getDeckCount()))
                                                .collectList(),
                                        gameRepository.countByPlayerId(id)
                                )
                                .map(tuple -> PageResponse.of(
                                        tuple.getT1(),
                                        page,
                                        size,
                                        tuple.getT2()
                                ))
                )
                .doOnSuccess(r -> log.info("Retrieved {} games for player: {}", r.content().size(), playerId))
                .doOnError(e -> log.error("Error retrieving games for player {}: {}", playerId, e.getMessage()));
    }

}
