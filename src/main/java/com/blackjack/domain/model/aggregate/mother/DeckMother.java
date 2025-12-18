package com.blackjack.domain.model.aggregate;

import com.blackjack.domain.model.valueobject.card.Card;
import com.blackjack.domain.model.valueobject.card.Rank;
import com.blackjack.domain.model.valueobject.card.Suit;
import com.blackjack.domain.model.valueobject.game.Deck;

import java.util.ArrayList;
import java.util.List;


public class DeckMother {
    
    public static Deck playerWillBust() {
        return withTopCards(
                new Card(Rank.KING, Suit.HEARTS),    
                new Card(Rank.QUEEN, Suit.SPADES),   
                new Card(Rank.FIVE, Suit.CLUBS),     
                new Card(Rank.KING, Suit.DIAMONDS)   
        );
    }
    
    public static Deck dealerWillBust() {
        return withTopCards(
                new Card(Rank.NINE, Suit.HEARTS),    
                new Card(Rank.NINE, Suit.SPADES),    
                new Card(Rank.TEN, Suit.CLUBS),      
                new Card(Rank.KING, Suit.DIAMONDS),  
                new Card(Rank.FIVE, Suit.HEARTS)     
        );
    }
    
    public static Deck playerWinsHigherValue() {
        return withTopCards(
                new Card(Rank.TEN, Suit.HEARTS),     
                new Card(Rank.KING, Suit.SPADES),    
                new Card(Rank.NINE, Suit.CLUBS),     
                new Card(Rank.NINE, Suit.DIAMONDS)   
        );
    }
    
    public static Deck dealerWinsHigherValue() {
        return withTopCards(
                new Card(Rank.NINE, Suit.HEARTS),    
                new Card(Rank.NINE, Suit.SPADES),    
                new Card(Rank.TEN, Suit.CLUBS),      
                new Card(Rank.KING, Suit.DIAMONDS)   
        );
    }
    
    public static Deck tieGame() {
        return withTopCards(
                new Card(Rank.TEN, Suit.HEARTS),     
                new Card(Rank.NINE, Suit.SPADES),    
                new Card(Rank.TEN, Suit.CLUBS),      
                new Card(Rank.NINE, Suit.DIAMONDS)   
        );
    }
    
    public static Deck playerBlackjack() {
        return withTopCards(
                new Card(Rank.ACE, Suit.HEARTS),     
                new Card(Rank.KING, Suit.SPADES),    
                new Card(Rank.TEN, Suit.CLUBS)       
        );
    }
    
    public static Deck dealerDrawsUntil17() {
        return withTopCards(
                new Card(Rank.NINE, Suit.HEARTS),    
                new Card(Rank.NINE, Suit.SPADES),    
                new Card(Rank.TEN, Suit.CLUBS),      
                new Card(Rank.SEVEN, Suit.DIAMONDS)  
        );
    }
    
    public static Deck withTopCards(Card... topCards) {
        List<Card> allCards = new ArrayList<>(List.of(topCards));
        
        Deck standardDeck = Deck.createUnshuffled();
        for (Card card : standardDeck.getCards()) {
            if (!allCards.contains(card) && allCards.size() < 52) {
                allCards.add(card);
            }
        }
        
        while (allCards.size() < 52) {
            Card filler = new Card(Rank.TWO, Suit.CLUBS);
            if (!allCards.contains(filler)) {
                allCards.add(filler);
            }
        }

        return Deck.reconstitute(allCards.subList(0, 52), 0);
    }
}
