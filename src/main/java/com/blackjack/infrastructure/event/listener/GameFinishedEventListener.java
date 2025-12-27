package com.blackjack.infrastructure.event.listener;

import com.blackjack.domain.event.GameFinishedEvent;
import com.blackjack.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameFinishedEventListener {

    private final PlayerRepository playerRepository;

    @Async
    @EventListener
    public void handleGameFinishedEvent(GameFinishedEvent event) {
        log.info("Handling GameFinishedEvent: gameId={}, playerId={}, status={}",
                event.gameId().value(),
                event.playerId().value(),
                event.finalStatus());

        playerRepository.findById(event.playerId())
                .flatMap(player -> {
                    log.debug("Found player: {} ({})", player.getName().value(), player.getId().value());

                    player.recordGameResult(event.finalStatus());

                    log.debug("Player stats updated - Games: {}, Wins: {}, Win Rate: {}%",
                            player.getGamesPlayed(),
                            player.getGamesWon(),
                            String.format("%.2f", player.getWinRate()));

                    return playerRepository.save(player);
                })
                .doOnSuccess(player ->
                        log.info("Player statistics updated successfully for playerId: {}",
                                event.playerId().value())
                )
                .doOnError(error ->
                        log.error("Error updating player statistics for playerId {}: {}",
                                event.playerId().value(), error.getMessage())
                )
                .subscribe();
    }
}