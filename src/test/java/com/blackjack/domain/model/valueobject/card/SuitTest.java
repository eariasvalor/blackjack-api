package com.blackjack.domain.model.valueobject.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Suit Enum Tests")
class SuitTest {

    @Test
    @DisplayName("Hearts and Diamonds should be red")
    void heartsAndDiamondsShouldBeRed() {
        assertThat(Suit.HEARTS.isRed()).isTrue();
        assertThat(Suit.DIAMONDS.isRed()).isTrue();

        assertThat(Suit.HEARTS.isBlack()).isFalse();
        assertThat(Suit.DIAMONDS.isBlack()).isFalse();
    }

    @Test
    @DisplayName("Clubs and Spades should be black")
    void clubsAndSpadesShouldBeBlack() {
        assertThat(Suit.CLUBS.isBlack()).isTrue();
        assertThat(Suit.SPADES.isBlack()).isTrue();

        assertThat(Suit.CLUBS.isRed()).isFalse();
        assertThat(Suit.SPADES.isRed()).isFalse();
    }

    @Test
    @DisplayName("Should have correct Unicode symbols")
    void shouldHaveCorrectUnicodeSymbols() {
        assertThat(Suit.HEARTS.getSymbol()).isEqualTo("♥");
        assertThat(Suit.DIAMONDS.getSymbol()).isEqualTo("♦");
        assertThat(Suit.CLUBS.getSymbol()).isEqualTo("♣");
        assertThat(Suit.SPADES.getSymbol()).isEqualTo("♠");
    }

    @Test
    @DisplayName("Should have exactly 4 suits")
    void shouldHaveExactly4Suits() {
        assertThat(Suit.values()).hasSize(4);
    }

    @Test
    @DisplayName("Should have correct display names")
    void shouldHaveCorrectDisplayNames() {
        assertThat(Suit.HEARTS.getDisplayName()).isEqualTo("Hearts");
        assertThat(Suit.DIAMONDS.getDisplayName()).isEqualTo("Diamonds");
        assertThat(Suit.CLUBS.getDisplayName()).isEqualTo("Clubs");
        assertThat(Suit.SPADES.getDisplayName()).isEqualTo("Spades");
    }
}