package com.blackjack.domain.model.aggregate.mother;


import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.valueobject.game.Deck;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;

public class GameMother {

    private static final PlayerId DEFAULT_PLAYER_ID = PlayerId.generate();
    private static final PlayerName DEFAULT_PLAYER_NAME = new PlayerName("TestPlayer");

    public static Game defaultGame() {
        return Game.create(DEFAULT_PLAYER_ID, DEFAULT_PLAYER_NAME);
    }

    public static Game withDeck(Deck deck) {
        return Game.createWithDeck(DEFAULT_PLAYER_ID, DEFAULT_PLAYER_NAME, deck);
    }

    public static Game withPlayer(PlayerId playerId, PlayerName playerName) {
        return Game.create(playerId, playerName);
    }

    public static Game withPlayerAndDeck(PlayerId playerId, PlayerName playerName, Deck deck) {
        return Game.createWithDeck(playerId, playerName, deck);
    }

    public static Game wherePlayerWillBust() {
        return withDeck(DeckMother.playerWillBust());
    }

    public static Game whereDealerWillBust() {
        return withDeck(DeckMother.dealerWillBust());
    }

    public static Game wherePlayerWins() {
        return withDeck(DeckMother.playerWinsHigherValue());
    }

    public static Game whereDealerWins() {
        return withDeck(DeckMother.dealerWinsHigherValue());
    }

    public static Game whereTie() {
        return withDeck(DeckMother.tieGame());
    }

    public static Game wherePlayerHasBlackjack() {
        return withDeck(DeckMother.playerBlackjack());
    }
}