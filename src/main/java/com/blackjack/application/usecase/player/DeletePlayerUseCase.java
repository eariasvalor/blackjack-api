package com.blackjack.application.usecase.player;

import com.blackjack.application.exception.PlayerNotFoundException;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.repository.GameRepository;
import com.blackjack.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletePlayerUseCase {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    public Mono<Void> execute(String playerId) {
        PlayerId id = PlayerId.from(playerId);

        return playerRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new PlayerNotFoundException(playerId));
                    }
                    return gameRepository.deleteByPlayerId(id)
                            .then(playerRepository.deleteById(id));
                });
    }
}
