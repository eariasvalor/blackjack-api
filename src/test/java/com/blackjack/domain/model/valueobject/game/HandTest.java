package com.blackjack.domain.model.valueobject.game;

import com.blackjack.domain.model.valueobject.card.Card;
import com.blackjack.domain.model.valueobject.card.Rank;
import com.blackjack.domain.model.valueobject.card.Suit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Hand Value Object Tests")
class HandTest {

    @Test
    @DisplayName("Should create empty hand")
    void shouldCreateEmptyHand() {
        Hand hand = new Hand();

        assertThat(hand.isEmpty()).isTrue();
        assertThat(hand.size()).isZero();
        assertThat(hand.calculateValue()).isZero();
    }

    @Test
    @DisplayName("Should add card to hand")
    void shouldAddCardToHand() {
        Hand hand = new Hand();
        Card card = new Card(Rank.SEVEN, Suit.SPADES);

        hand.addCard(card);

        assertThat(hand.size()).isEqualTo(1);
        assertThat(hand.getCards()).containsExactly(card);
    }

    @Test
    @DisplayName("Should calculate value with number cards")
    void shouldCalculateValueWithNumberCards() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.SEVEN, Suit.SPADES));
        hand.addCard(new Card(Rank.NINE, Suit.HEARTS));

        int value = hand.calculateValue();

        assertThat(value).isEqualTo(16);
    }

    @Test
    @DisplayName("Should calculate Ace as 11 when possible")
    void shouldCalculateAceAs11WhenPossible() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));
        hand.addCard(new Card(Rank.SEVEN, Suit.DIAMONDS));

        int value = hand.calculateValue();

        assertThat(value).isEqualTo(18);
    }

    @Test
    @DisplayName("Should calculate Ace as 1 to avoid bust")
    void shouldCalculateAceAs1ToAvoidBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));
        hand.addCard(new Card(Rank.SEVEN, Suit.DIAMONDS));
        hand.addCard(new Card(Rank.FIVE, Suit.CLUBS));

        int value = hand.calculateValue();

        assertThat(value).isEqualTo(13);
    }

    @Test
    @DisplayName("Should handle multiple Aces correctly")
    void shouldHandleMultipleAcesCorrectly() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));
        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        hand.addCard(new Card(Rank.NINE, Suit.CLUBS));

        int value = hand.calculateValue();

        assertThat(value).isEqualTo(21);
    }

    @Test
    @DisplayName("Should detect bust when over 21")
    void shouldDetectBustWhenOver21() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.TEN, Suit.HEARTS));
        hand.addCard(new Card(Rank.KING, Suit.SPADES));
        hand.addCard(new Card(Rank.FIVE, Suit.CLUBS));

        assertThat(hand.isBusted()).isTrue();
        assertThat(hand.calculateValue()).isEqualTo(25);
    }

    @Test
    @DisplayName("Should not be busted with 21")
    void shouldNotBeBustedWith21() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));
        hand.addCard(new Card(Rank.KING, Suit.SPADES));

        assertThat(hand.isBusted()).isFalse();
        assertThat(hand.calculateValue()).isEqualTo(21);
    }

    @Test
    @DisplayName("Should detect Blackjack")
    void shouldDetectBlackjack() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.CLUBS));
        hand.addCard(new Card(Rank.KING, Suit.SPADES));

        assertThat(hand.isBlackjack()).isTrue();
        assertThat(hand.calculateValue()).isEqualTo(21);

    }

    @Test
    @DisplayName("Should NOT be Blackjack with 21 but more than 2 cards")
    void shouldNotBeBlackjackWith21ButMoreThan2Cards() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.SEVEN, Suit.HEARTS));
        hand.addCard(new Card(Rank.SEVEN, Suit.SPADES));
        hand.addCard(new Card(Rank.SEVEN, Suit.CLUBS));

        assertThat(hand.calculateValue()).isEqualTo(21);
        assertThat(hand.isBlackjack()).isFalse();
    }

    @Test
    @DisplayName("Should calculate face cards as 10")
    void shouldCalculateFaceCardsAs10() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.JACK, Suit.HEARTS));
        hand.addCard(new Card(Rank.QUEEN, Suit.SPADES));
        hand.addCard(new Card(Rank.KING, Suit.CLUBS));

        assertThat(hand.calculateValue()).isEqualTo(30);
        assertThat(hand.isBusted()).isTrue();
    }

    @Test
    @DisplayName("Should get first card")
    void shouldGetFirstCard() {
        Hand hand = new Hand();
        Card firstCard = new Card(Rank.SEVEN, Suit.SPADES);
        Card secondCard = new Card(Rank.NINE, Suit.HEARTS);
        hand.addCard(firstCard);
        hand.addCard(secondCard);

        assertThat(hand.getFirstCard()).isEqualTo(firstCard);
    }

    @Test
    @DisplayName("Should return null for first card when empty")
    void shouldReturnNullForFirstCardWhenEmpty() {
        Hand hand = new Hand();

        assertThat(hand.getFirstCard()).isNull();
    }

    @Test
    @DisplayName("Should return immutable list of cards")
    void shouldReturnImmutableListOfCards() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.SEVEN, Suit.SPADES));

        List<Card> cards = hand.getCards();

        assertThatThrownBy(() -> cards.add(new Card(Rank.NINE, Suit.HEARTS)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should create hand from list of cards")
    void shouldCreateHandFromListOfCards() {
        List<Card> cards = List.of(
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.NINE, Suit.HEARTS)
        );

        Hand hand = Hand.from(cards);

        assertThat(hand.size()).isEqualTo(2);
        assertThat(hand.calculateValue()).isEqualTo(16);
    }

    @Test
    @DisplayName("Should throw exception when adding null card")
    void shouldThrowExceptionWhenAddingNullCard() {
        Hand hand = new Hand();

        assertThatThrownBy(() -> hand.addCard(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Card cannot be null");
    }

    @Test
    @DisplayName("Two hands with same cards should be equal")
    void twoHandsWithSameCardsShouldBeEqual() {
        Hand hand1 = new Hand();
        hand1.addCard(new Card(Rank.SEVEN, Suit.SPADES));
        hand1.addCard(new Card(Rank.NINE, Suit.HEARTS));

        Hand hand2 = new Hand();
        hand2.addCard(new Card(Rank.SEVEN, Suit.SPADES));
        hand2.addCard(new Card(Rank.NINE, Suit.HEARTS));

        assertThat(hand1).isEqualTo(hand2);
        assertThat(hand1.hashCode()).isEqualTo(hand2.hashCode());
    }
}