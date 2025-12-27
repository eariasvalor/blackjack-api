package com.blackjack.infrastructure.event;

import com.blackjack.domain.event.GameFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishGameFinishedEvent(GameFinishedEvent event) {
        log.info("Publishing GameFinishedEvent: gameId={}, status={}",
                event.gameId().value(), event.finalStatus());

        applicationEventPublisher.publishEvent(event);

        log.debug("GameFinishedEvent published successfully");
    }
}