package com.blackjack.domain.model.aggregate;

import com.blackjack.domain.event.DomainEvent;
import com.blackjack.domain.event.GameFinishedEvent;
import com.blackjack.domain.model.aggregate.mother.GameMother;
import com.blackjack.domain.model.valueobject.game.GameStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Game Domain Events Tests")
class GameEventsTest {

    @Test
    @DisplayName("Should emit GameFinishedEvent when player busts")
    void shouldEmitGameFinishedEventWhenPlayerBusts() {
        Game game = GameMother.wherePlayerWillBust();

        game.hit();
        List<DomainEvent> events = game.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(GameFinishedEvent.class);

        GameFinishedEvent event = (GameFinishedEvent) events.get(0);
        assertThat(event.gameId()).isEqualTo(game.getId());
        assertThat(event.playerId()).isEqualTo(game.getPlayerId());
        assertThat(event.finalStatus()).isEqualTo(GameStatus.DEALER_WIN);
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("Should emit GameFinishedEvent when game ends with stand")
    void shouldEmitGameFinishedEventWhenGameEndsWithStand() {
        Game game = GameMother.defaultGame();

        game.stand();

        List<DomainEvent> events = game.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(GameFinishedEvent.class);

        GameFinishedEvent event = (GameFinishedEvent) events.get(0);
        assertThat(event.finalStatus().isFinished()).isTrue();
    }

    @Test
    @DisplayName("Should not emit event when game is still playing")
    void shouldNotEmitEventWhenGameIsStillPlaying() {
        Game game = GameMother.defaultGame();

        game.hit();
        if (!game.getStatus().isFinished()) {
            assertThat(game.getDomainEvents()).isEmpty();
        }
    }

    @Test
    @DisplayName("Should clear domain events after being read")
    void shouldClearDomainEventsAfterBeingRead() {
        Game game = GameMother.wherePlayerWillBust();
        game.hit();
        assertThat(game.getDomainEvents()).isNotEmpty();

        game.clearDomainEvents();

        assertThat(game.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Should return immutable list of events")
    void shouldReturnImmutableListOfEvents() {
        Game game = GameMother.wherePlayerWillBust();
        game.hit();

        List<DomainEvent> events = game.getDomainEvents();

        assertThatThrownBy(() -> events.clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should not emit event if status is not finished")
    void shouldNotEmitEventIfStatusIsNotFinished() {
        Game game = GameMother.defaultGame();

        game.hit();

        if (game.getStatus() == GameStatus.PLAYING) {
            assertThat(game.getDomainEvents()).isEmpty();
        }
    }
}
