package com.blackjack.domain.model.valueobject.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Card Value Object Tests")
class CardTest {

    @Test
    @DisplayName("Should create a valid card")
    void shouldCreateValidCard() {
        Card card = new Card(Rank.SEVEN, Suit.SPADES);

        assertThat(card.rank()).isEqualTo(Rank.SEVEN);
        assertThat(card.suit()).isEqualTo(Suit.SPADES);
    }

    @Test
    @DisplayName("Should get correct value for number cards")
    void shouldGetCorrectValueForNumberCards() {
        Card seven = new Card(Rank.SEVEN, Suit.SPADES);
        Card nine = new Card(Rank.NINE, Suit.HEARTS);

        assertThat(seven.getValue()).isEqualTo(7);
        assertThat(nine.getValue()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should get value 10 for face cards")
    void shouldGetValue10ForFaceCards() {
        Card jack = new Card(Rank.JACK, Suit.CLUBS);
        Card queen = new Card(Rank.QUEEN, Suit.DIAMONDS);
        Card king = new Card(Rank.KING, Suit.HEARTS);

        assertThat(jack.getValue()).isEqualTo(10);
        assertThat(queen.getValue()).isEqualTo(10);
        assertThat(king.getValue()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should get value 1 for Ace")
    void shouldGetValue1ForAce() {
        Card ace = new Card(Rank.ACE, Suit.SPADES);

        assertThat(ace.getValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should get correct symbol")
    void shouldGetCorrectSymbol() {
        Card sevenOfSpades = new Card(Rank.SEVEN, Suit.SPADES);
        Card aceOfHearts = new Card(Rank.ACE, Suit.HEARTS);
        Card kingOfDiamonds = new Card(Rank.KING, Suit.DIAMONDS);

        assertThat(sevenOfSpades.getSymbol()).isEqualTo("7♠");
        assertThat(aceOfHearts.getSymbol()).isEqualTo("A♥");
        assertThat(kingOfDiamonds.getSymbol()).isEqualTo("K♦");
    }

    @Test
    @DisplayName("Should identify ace correctly")
    void shouldIdentifyAceCorrectly() {
        Card ace = new Card(Rank.ACE, Suit.HEARTS);
        Card notAce = new Card(Rank.SEVEN, Suit.SPADES);

        assertThat(ace.isAce()).isTrue();
        assertThat(notAce.isAce()).isFalse();
    }

    @Test
    @DisplayName("Should identify face cards correctly")
    void shouldIdentifyFaceCardsCorrectly() {
        Card jack = new Card(Rank.JACK, Suit.CLUBS);
        Card queen = new Card(Rank.QUEEN, Suit.DIAMONDS);
        Card king = new Card(Rank.KING, Suit.HEARTS);
        Card notFaceCard = new Card(Rank.SEVEN, Suit.SPADES);

        assertThat(jack.isFaceCard()).isTrue();
        assertThat(queen.isFaceCard()).isTrue();
        assertThat(king.isFaceCard()).isTrue();
        assertThat(notFaceCard.isFaceCard()).isFalse();
    }

    @Test
    @DisplayName("Should get correct display string")
    void shouldGetCorrectDisplayString() {
        Card card = new Card(Rank.SEVEN, Suit.SPADES);

        assertThat(card.toDisplayString()).isEqualTo("SEVEN of Spades");
    }

    @Test
    @DisplayName("ToString should return symbol")
    void toStringShouldReturnSymbol() {
        Card card = new Card(Rank.SEVEN, Suit.SPADES);

        assertThat(card.toString()).isEqualTo("7♠");
    }

    @Test
    @DisplayName("Two cards with same rank and suit should be equal")
    void twoCardsWithSameRankAndSuitShouldBeEqual() {
        Card card1 = new Card(Rank.SEVEN, Suit.SPADES);
        Card card2 = new Card(Rank.SEVEN, Suit.SPADES);

        assertThat(card1).isEqualTo(card2);
        assertThat(card1.hashCode()).isEqualTo(card2.hashCode());
    }

    @Test
    @DisplayName("Two cards with different rank should not be equal")
    void twoCardsWithDifferentRankShouldNotBeEqual() {
        Card card1 = new Card(Rank.SEVEN, Suit.SPADES);
        Card card2 = new Card(Rank.EIGHT, Suit.SPADES);

        assertThat(card1).isNotEqualTo(card2);
    }

    @Test
    @DisplayName("Two cards with different suit should not be equal")
    void twoCardsWithDifferentSuitShouldNotBeEqual() {
        Card card1 = new Card(Rank.SEVEN, Suit.SPADES);
        Card card2 = new Card(Rank.SEVEN, Suit.HEARTS);

        assertThat(card1).isNotEqualTo(card2);
    }

    @Test
    @DisplayName("Should throw exception when rank is null")
    void shouldThrowExceptionWhenRankIsNull() {
        assertThatThrownBy(() -> new Card(null, Suit.SPADES))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Rank cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when suit is null")
    void shouldThrowExceptionWhenSuitIsNull() {
        assertThatThrownBy(() -> new Card(Rank.SEVEN, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Suit cannot be null");
    }
}