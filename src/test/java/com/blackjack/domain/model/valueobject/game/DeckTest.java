package com.blackjack.domain.model.valueobject.game;

import com.blackjack.domain.model.valueobject.card.Card;
import com.blackjack.domain.model.valueobject.card.Rank;
import com.blackjack.domain.model.valueobject.card.Suit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Deck Value Object Tests")
class DeckTest {

    @Test
    @DisplayName("Should create deck with 52 cards")
    void shouldCreateDeckWith52Cards() {
        Deck deck = Deck.createAndShuffle();

        assertThat(deck.size()).isEqualTo(52);
        assertThat(deck.remainingCards()).isEqualTo(52);
        assertThat(deck.getCurrentIndex()).isZero();
    }

    @Test
    @DisplayName("Should create unshuffled deck in standard order")
    void shouldCreateUnshuffledDeckInStandardOrder() {
        Deck deck = Deck.createUnshuffled();

        assertThat(deck.size()).isEqualTo(52);

        Card firstCard = deck.draw();
        assertThat(firstCard.rank()).isEqualTo(Rank.ACE);
        assertThat(firstCard.suit()).isEqualTo(Suit.HEARTS);
    }

    @Test
    @DisplayName("Should have all 52 unique cards")
    void shouldHaveAll52UniqueCards() {
        Deck deck = Deck.createAndShuffle();
        Set<Card> uniqueCards = new HashSet<>();

        while (!deck.isEmpty()) {
            uniqueCards.add(deck.draw());
        }

        assertThat(uniqueCards).hasSize(52);
    }

    @Test
    @DisplayName("Should draw cards and advance index")
    void shouldDrawCardsAndAdvanceIndex() {
        Deck deck = Deck.createAndShuffle();

        deck.draw();
        deck.draw();
        deck.draw();

        assertThat(deck.getCurrentIndex()).isEqualTo(3);
        assertThat(deck.remainingCards()).isEqualTo(49);
    }

    @Test
    @DisplayName("Should throw exception when drawing from empty deck")
    void shouldThrowExceptionWhenDrawingFromEmptyDeck() {
        Deck deck = Deck.createAndShuffle();

        for (int i = 0; i < 52; i++) {
            deck.draw();
        }

        assertThatThrownBy(deck::draw)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No more cards");
    }

    @Test
    @DisplayName("Should be empty after drawing all cards")
    void shouldBeEmptyAfterDrawingAllCards() {
        Deck deck = Deck.createAndShuffle();

        for (int i = 0; i < 52; i++) {
            deck.draw();
        }

        assertThat(deck.isEmpty()).isTrue();
        assertThat(deck.remainingCards()).isZero();
    }

    @Test
    @DisplayName("Should not be empty initially")
    void shouldNotBeEmptyInitially() {
        Deck deck = Deck.createAndShuffle();

        assertThat(deck.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Should reconstitute deck from cards and index")
    void shouldReconstituteDeckFromCardsAndIndex() {
        Deck originalDeck = Deck.createAndShuffle();
        List<Card> originalCards = originalDeck.getCards();

        originalDeck.draw();
        originalDeck.draw();
        originalDeck.draw();

        Deck reconstitutedDeck = Deck.reconstitute(originalCards, 3);

        assertThat(reconstitutedDeck.getCurrentIndex()).isEqualTo(3);
        assertThat(reconstitutedDeck.remainingCards()).isEqualTo(49);
        assertThat(reconstitutedDeck.getCards()).isEqualTo(originalCards);
    }

    @Test
    @DisplayName("Should throw exception when reconstituting with wrong card count")
    void shouldThrowExceptionWhenReconstitutingWithWrongCardCount() {
        List<Card> cards = List.of(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.KING, Suit.SPADES)
        );

        assertThatThrownBy(() -> Deck.reconstitute(cards, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must have exactly 52 cards");
    }

    @Test
    @DisplayName("Should throw exception when reconstituting with invalid index")
    void shouldThrowExceptionWhenReconstitutingWithInvalidIndex() {
        Deck deck = Deck.createAndShuffle();
        List<Card> cards = deck.getCards();

        assertThatThrownBy(() -> Deck.reconstitute(cards, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be between 0 and");

        assertThatThrownBy(() -> Deck.reconstitute(cards, 53))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be between 0 and");
    }

    @Test
    @DisplayName("Should return immutable list of cards")
    void shouldReturnImmutableListOfCards() {
        Deck deck = Deck.createAndShuffle();

        List<Card> cards = deck.getCards();

        assertThatThrownBy(() -> cards.add(new Card(Rank.ACE, Suit.HEARTS)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Shuffled decks should be different")
    void shuffledDecksShouldBeDifferent() {
        Deck deck1 = Deck.createAndShuffle();
        Deck deck2 = Deck.createAndShuffle();

        assertThat(deck1.getCards()).isNotEqualTo(deck2.getCards());
    }

    @Test
    @DisplayName("Should have all ranks and suits")
    void shouldHaveAllRanksAndSuits() {
        Deck deck = Deck.createAndShuffle();
        Set<Rank> ranks = new HashSet<>();
        Set<Suit> suits = new HashSet<>();

        for (Card card : deck.getCards()) {
            ranks.add(card.rank());
            suits.add(card.suit());
        }

        assertThat(ranks).hasSize(13);
        assertThat(suits).hasSize(4);
    }

    @Test
    @DisplayName("Two decks with same cards and index should be equal")
    void twoDecksWithSameCardsAndIndexShouldBeEqual() {
        Deck deck1 = Deck.createUnshuffled();
        deck1.draw();

        Deck deck2 = Deck.reconstitute(deck1.getCards(), deck1.getCurrentIndex());

        assertThat(deck1).isEqualTo(deck2);
        assertThat(deck1.hashCode()).isEqualTo(deck2.hashCode());
    }
}