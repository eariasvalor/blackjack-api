package com.blackjack.domain.model.valueobject.turn;

import com.blackjack.domain.model.valueobject.card.Card;
import com.blackjack.domain.model.valueobject.card.Rank;
import com.blackjack.domain.model.valueobject.card.Suit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Turn Value Object Tests")
class TurnTest {

    @Test
    @DisplayName("Should create player HIT turn")
    void shouldCreatePlayerHitTurn() {
        Card card = new Card(Rank.SEVEN, Suit.SPADES);

        Turn turn = Turn.playerHit(1, card, 18);

        assertThat(turn.turnNumber()).isEqualTo(1);
        assertThat(turn.type()).isEqualTo(TurnType.PLAYER_HIT);
        assertThat(turn.owner()).isEqualTo(TurnOwner.PLAYER);
        assertThat(turn.cardDrawn()).isEqualTo(card);
        assertThat(turn.handValue()).isEqualTo(18);
        assertThat(turn.hasCard()).isTrue();
    }

    @Test
    @DisplayName("Should create player STAND turn")
    void shouldCreatePlayerStandTurn() {
        Turn turn = Turn.playerStand(2, 19);

        assertThat(turn.turnNumber()).isEqualTo(2);
        assertThat(turn.type()).isEqualTo(TurnType.PLAYER_STAND);
        assertThat(turn.owner()).isEqualTo(TurnOwner.PLAYER);
        assertThat(turn.cardDrawn()).isNull();
        assertThat(turn.handValue()).isEqualTo(19);
        assertThat(turn.hasCard()).isFalse();
    }

    @Test
    @DisplayName("Should create dealer HIT turn")
    void shouldCreateDealerHitTurn() {
        Card card = new Card(Rank.KING, Suit.HEARTS);

        Turn turn = Turn.dealerHit(3, card, 17);

        assertThat(turn.turnNumber()).isEqualTo(3);
        assertThat(turn.type()).isEqualTo(TurnType.DEALER_HIT);
        assertThat(turn.owner()).isEqualTo(TurnOwner.DEALER);
        assertThat(turn.cardDrawn()).isEqualTo(card);
        assertThat(turn.handValue()).isEqualTo(17);
        assertThat(turn.hasCard()).isTrue();
    }

    @Test
    @DisplayName("Should create dealer STAND turn")
    void shouldCreateDealerStandTurn() {
        Turn turn = Turn.dealerStand(4, 18);

        assertThat(turn.turnNumber()).isEqualTo(4);
        assertThat(turn.type()).isEqualTo(TurnType.DEALER_STAND);
        assertThat(turn.owner()).isEqualTo(TurnOwner.DEALER);
        assertThat(turn.cardDrawn()).isNull();
        assertThat(turn.handValue()).isEqualTo(18);
        assertThat(turn.hasCard()).isFalse();
    }

    @Test
    @DisplayName("Should create initial deal turn")
    void shouldCreateInitialDealTurn() {
        Turn turn = Turn.initialDeal(1);

        assertThat(turn.turnNumber()).isEqualTo(1);
        assertThat(turn.type()).isEqualTo(TurnType.INITIAL_DEAL);
        assertThat(turn.owner()).isEqualTo(TurnOwner.SYSTEM);
        assertThat(turn.cardDrawn()).isNull();
        assertThat(turn.handValue()).isZero();
        assertThat(turn.hasCard()).isFalse();
    }

    @Test
    @DisplayName("Should generate correct description")
    void shouldGenerateCorrectDescription() {
        Card card = new Card(Rank.SEVEN, Suit.SPADES);
        Turn hitTurn = Turn.playerHit(1, card, 18);
        Turn standTurn = Turn.playerStand(2, 18);

        assertThat(hitTurn.getDescription()).contains("Player", "hits", "7♠", "18");
        assertThat(standTurn.getDescription()).contains("Player", "stands", "18");
    }

    @Test
    @DisplayName("Should throw exception when turn number is zero")
    void shouldThrowExceptionWhenTurnNumberIsZero() {
        assertThatThrownBy(() -> new Turn(
                0,
                TurnType.PLAYER_HIT,
                TurnOwner.PLAYER,
                new Card(Rank.SEVEN, Suit.SPADES),
                18,
                LocalDateTime.now()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Turn number must be positive");
    }

    @Test
    @DisplayName("Should throw exception when turn number is negative")
    void shouldThrowExceptionWhenTurnNumberIsNegative() {
        assertThatThrownBy(() -> new Turn(
                -1,
                TurnType.PLAYER_HIT,
                TurnOwner.PLAYER,
                new Card(Rank.SEVEN, Suit.SPADES),
                18,
                LocalDateTime.now()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Turn number must be positive");
    }

    @Test
    @DisplayName("Should throw exception when type is null")
    void shouldThrowExceptionWhenTypeIsNull() {
        assertThatThrownBy(() -> new Turn(
                1,
                null,
                TurnOwner.PLAYER,
                new Card(Rank.SEVEN, Suit.SPADES),
                18,
                LocalDateTime.now()
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Turn type cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when owner is null")
    void shouldThrowExceptionWhenOwnerIsNull() {
        assertThatThrownBy(() -> new Turn(
                1,
                TurnType.PLAYER_HIT,
                null,
                new Card(Rank.SEVEN, Suit.SPADES),
                18,
                LocalDateTime.now()
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Turn owner cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when hand value is negative")
    void shouldThrowExceptionWhenHandValueIsNegative() {
        assertThatThrownBy(() -> new Turn(
                1,
                TurnType.PLAYER_HIT,
                TurnOwner.PLAYER,
                new Card(Rank.SEVEN, Suit.SPADES),
                -1,
                LocalDateTime.now()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hand value cannot be negative");
    }

    @Test
    @DisplayName("Two turns with same values should be equal")
    void twoTurnsWithSameValuesShouldBeEqual() {
        Card card = new Card(Rank.SEVEN, Suit.SPADES);
        LocalDateTime now = LocalDateTime.now();

        Turn turn1 = new Turn(1, TurnType.PLAYER_HIT, TurnOwner.PLAYER, card, 18, now);
        Turn turn2 = new Turn(1, TurnType.PLAYER_HIT, TurnOwner.PLAYER, card, 18, now);

        assertThat(turn1).isEqualTo(turn2);
        assertThat(turn1.hashCode()).isEqualTo(turn2.hashCode());
    }
}