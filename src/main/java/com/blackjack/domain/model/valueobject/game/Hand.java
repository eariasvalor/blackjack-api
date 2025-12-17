package com.blackjack.domain.model.valueobject.game;

import com.blackjack.domain.model.valueobject.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Hand {

    private static final int BLACKJACK_VALUE = 21;
    private static final int BLACKJACK_CARDS = 2;
    private static final int ACE_HIGH_VALUE = 11;
    private static final int ACE_LOW_VALUE = 1;

    private final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    private Hand(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public static Hand from(List<Card> cards) {
        Objects.requireNonNull(cards, "Cards cannot be null");
        return new Hand(cards);
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card, "Card cannot be null");
        this.cards.add(card);
    }

    public int calculateValue() {
        int value = 0;
        int aceCount = 0;

        for (Card card : cards) {
            value += card.getValue();
            if (card.isAce()) {
                aceCount++;
            }
        }

        while (aceCount > 0 && value + 10 <= BLACKJACK_VALUE) {
            value += 10;
            aceCount--;
        }

        return value;
    }

    public boolean isBusted() {
        return calculateValue() > BLACKJACK_VALUE;
    }

    public boolean isBlackjack() {
        return cards.size() == BLACKJACK_CARDS && calculateValue() == BLACKJACK_VALUE;
    }

    public boolean hasValue(int value) {
        return calculateValue() == value;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Card getFirstCard() {
        return cards.isEmpty() ? null : cards.get(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hand hand = (Hand) o;
        return Objects.equals(cards, hand.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cards);
    }

    @Override
    public String toString() {
        return "Hand{" +
                "cards=" + cards +
                ", value=" + calculateValue() +
                '}';
    }
}