package com.blackjack.application.usecase.game;

import com.blackjack.application.dto.request.CreateGameRequest;
import com.blackjack.application.dto.response.GameResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateGameUseCase {


    public Mono<GameResponse> execute(CreateGameRequest request) {
        // TODO: Implement logic
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
