package com.blackjack.domain.model.valueobject.game;

import com.blackjack.domain.model.valueobject.card.Card;
import com.blackjack.domain.model.valueobject.card.Rank;
import com.blackjack.domain.model.valueobject.card.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Deck {

    private static final int CARDS_PER_DECK = 52;

    private final List<Card> cards;
    private int currentIndex;

    private Deck(List<Card> cards, int currentIndex) {
        this.cards = List.copyOf(cards);
        this.currentIndex = currentIndex;
    }

    public static Deck createAndShuffle() {
        return createAndShuffle(DeckCount.of(1));
    }

    public static Deck createAndShuffle(DeckCount count) {
        List<Card> cards = createCards(count);
        Collections.shuffle(cards);
        return new Deck(cards, 0);
    }

    public static Deck createUnshuffled() {
        List<Card> cards = createStandardDeck();
        return new Deck(cards, 0);
    }

    public static Deck reconstitute(List<Card> cards, int currentIndex) {
        Objects.requireNonNull(cards, "Cards cannot be null");
        if (cards.size() == 0 || cards.size() % CARDS_PER_DECK != 0) {
            throw new IllegalArgumentException(
                    "Deck size must be a multiple of " + CARDS_PER_DECK + ", got " + cards.size()
            );
        }
        if (currentIndex < 0 || currentIndex > cards.size()) {
            throw new IllegalArgumentException(
                    "Current index must be between 0 and " + cards.size()
            );
        }
        return new Deck(cards, currentIndex);
    }

    private static List<Card> createCards(DeckCount count) {
        int totalCards = 52 * count.value();
        List<Card> cards = new ArrayList<>(totalCards);

        for (int i = 0; i < count.value(); i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(rank, suit));
                }
            }
        }
        return cards;
    }

    public Card draw() {
        if (isEmpty()) {
            throw new IllegalStateException("No more cards in the deck");
        }

        Card card = cards.get(currentIndex);
        currentIndex++;

        return card;
    }

    public boolean isEmpty() {
        return currentIndex >= cards.size();
    }

    public int remainingCards() {
        return cards.size() - currentIndex;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int size() {
        return cards.size();
    }

    private static List<Card> createStandardDeck() {
        List<Card> cards = new ArrayList<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }

        return cards;
    }

    public DeckCount getDeckCount(){
        return DeckCount.of(cards.size()/CARDS_PER_DECK);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        return currentIndex == deck.currentIndex && Objects.equals(cards, deck.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cards, currentIndex);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "totalCards=" + cards.size() +
                ", remainingCards=" + remainingCards() +
                ", currentIndex=" + currentIndex +
                '}';
    }
}