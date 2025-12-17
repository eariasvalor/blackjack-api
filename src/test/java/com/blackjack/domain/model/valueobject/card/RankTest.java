package com.blackjack.domain.model.valueobject.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Rank Enum Tests")
class RankTest {

    @Test
    @DisplayName("Ace should have value 1")
    void aceShouldHaveValue1() {
        assertThat(Rank.ACE.getValue()).isEqualTo(1);
        assertThat(Rank.ACE.getSymbol()).isEqualTo("A");
    }

    @Test
    @DisplayName("Number cards should have their face value")
    void numberCardsShouldHaveFaceValue() {
        assertThat(Rank.TWO.getValue()).isEqualTo(2);
        assertThat(Rank.FIVE.getValue()).isEqualTo(5);
        assertThat(Rank.NINE.getValue()).isEqualTo(9);
    }

    @Test
    @DisplayName("Face cards should have value 10")
    void faceCardsShouldHaveValue10() {
        assertThat(Rank.JACK.getValue()).isEqualTo(10);
        assertThat(Rank.QUEEN.getValue()).isEqualTo(10);
        assertThat(Rank.KING.getValue()).isEqualTo(10);
    }

    @Test
    @DisplayName("Only ACE should be identified as ace")
    void onlyAceShouldBeIdentifiedAsAce() {
        assertThat(Rank.ACE.isAce()).isTrue();
        assertThat(Rank.TWO.isAce()).isFalse();
        assertThat(Rank.KING.isAce()).isFalse();
    }

    @Test
    @DisplayName("Only J, Q, K should be face cards")
    void onlyJQKShouldBeFaceCards() {
        assertThat(Rank.JACK.isFaceCard()).isTrue();
        assertThat(Rank.QUEEN.isFaceCard()).isTrue();
        assertThat(Rank.KING.isFaceCard()).isTrue();

        assertThat(Rank.ACE.isFaceCard()).isFalse();
        assertThat(Rank.TEN.isFaceCard()).isFalse();
    }

    @Test
    @DisplayName("Should have exactly 13 ranks")
    void shouldHaveExactly13Ranks() {
        assertThat(Rank.values()).hasSize(13);
    }
}