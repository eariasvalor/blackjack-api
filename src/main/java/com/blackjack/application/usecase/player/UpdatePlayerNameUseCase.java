package com.blackjack.application.usecase.player;

import com.blackjack.application.dto.request.UpdatePlayerRequest;
import com.blackjack.application.dto.response.PlayerResponse;
import com.blackjack.application.exception.PlayerNotFoundException;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePlayerNameUseCase {

    private final PlayerRepository playerRepository;

    public Mono<PlayerResponse> execute(String playerId, UpdatePlayerRequest request) {
        log.info("Updating player name for playerId: {} to: {}", playerId, request.playerName());

        return playerRepository.findById(PlayerId.from(playerId))
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .flatMap(player -> {
                    String oldName = player.getName().value();
                    player.updateName(new PlayerName(request.playerName()));
                    log.debug("Player {} name changed from '{}' to '{}'", playerId, oldName, request.playerName());
                    return playerRepository.save(player);
                })
                .map(player -> new PlayerResponse(
                        player.getId().value(),
                        player.getName().value(),
                        player.getGamesPlayed(),
                        player.getGamesWon(),
                        player.getGamesLost(),
                        player.getGamesTied(),
                        player.getWinRate() / 100.0,
                        player.getCreatedAt(),
                        player.getUpdatedAt()
                ))
                .doOnSuccess(response -> log.info("Player name updated successfully: {}", playerId))
                .doOnError(error -> log.error("Error updating player {}: {}", playerId, error.getMessage()));
    }
}