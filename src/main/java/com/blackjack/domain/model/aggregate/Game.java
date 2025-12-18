package com.blackjack.domain.model.aggregate;

import com.blackjack.domain.model.valueobject.card.Card;
import com.blackjack.domain.model.valueobject.game.*;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.domain.model.valueobject.turn.Turn;
import com.blackjack.domain.event.DomainEvent;
import com.blackjack.domain.event.GameFinishedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Game {

    private static final int DEALER_MUST_HIT_BELOW = 17;

    private final GameId id;

    private final PlayerId playerId;

    private final Hand playerHand;
    private final Hand dealerHand;
    private final Deck deck;
    private GameStatus status;

    private final List<Turn> turnHistory;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final List<DomainEvent> domainEvents;

    private Game(GameId id, PlayerId playerId, Hand playerHand, Hand dealerHand, Deck deck, GameStatus status, List<Turn> turnHistory, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "GameId cannot be null");
        this.playerId = Objects.requireNonNull(playerId, "PlayerId cannot be null");
        this.playerHand = Objects.requireNonNull(playerHand, "Player hand cannot be null");
        this.dealerHand = Objects.requireNonNull(dealerHand, "Dealer hand cannot be null");
        this.deck = Objects.requireNonNull(deck, "Deck cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.turnHistory = new ArrayList<>(Objects.requireNonNull(turnHistory, "Turn history cannot be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");
        this.domainEvents = new ArrayList<>();
    }

    public static Game create(PlayerId playerId) {
        Deck deck = Deck.createAndShuffle();
        return createWithDeck(playerId, deck);
    }

    public static Game createWithDeck(PlayerId playerId, Deck deck) {
        GameId gameId = GameId.generate();
        Hand playerHand = new Hand();
        Hand dealerHand = new Hand();
        GameStatus status = GameStatus.PLAYING;
        List<Turn> turnHistory = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        Game game = new Game(
                gameId, playerId,
                playerHand, dealerHand, deck,
                status, turnHistory,
                now, now
        );

        game.dealInitialCards();

        return game;
    }

    public Turn hit() {
        validateGameIsActive();

        Card card = deck.draw();
        playerHand.addCard(card);

        if (playerHand.isBusted()) {
            this.status = GameStatus.DEALER_WIN;
            emitGameFinishedEvent();
        }

        Turn turn = Turn.playerHit(turnHistory.size() + 1, card, playerHand.calculateValue());
        turnHistory.add(turn);

        this.updatedAt = LocalDateTime.now();

        return turn;
    }

    public List<Turn> stand() {
        validateGameIsActive();

        List<Turn> dealerTurns = new ArrayList<>();

        Turn playerStandTurn = Turn.playerStand(turnHistory.size() + 1, playerHand.calculateValue());
        turnHistory.add(playerStandTurn);
        dealerTurns.add(playerStandTurn);

        while (dealerHand.calculateValue() < DEALER_MUST_HIT_BELOW) {
            Card card = deck.draw();
            dealerHand.addCard(card);

            Turn dealerHitTurn = Turn.dealerHit(turnHistory.size() + 1, card, dealerHand.calculateValue());
            turnHistory.add(dealerHitTurn);
            dealerTurns.add(dealerHitTurn);
        }

        Turn dealerStandTurn = Turn.dealerStand(turnHistory.size() + 1, dealerHand.calculateValue());
        turnHistory.add(dealerStandTurn);
        dealerTurns.add(dealerStandTurn);

        this.status = calculateResult();
        this.updatedAt = LocalDateTime.now();

        emitGameFinishedEvent();

        return dealerTurns;
    }

    private void dealInitialCards() {
        playerHand.addCard(deck.draw());
        playerHand.addCard(deck.draw());

        dealerHand.addCard(deck.draw());
    }

    private GameStatus calculateResult() {
        int playerValue = playerHand.calculateValue();
        int dealerValue = dealerHand.calculateValue();

        if (dealerHand.isBusted()) {
            return GameStatus.PLAYER_WIN;
        }

        if (playerValue > dealerValue) {
            return GameStatus.PLAYER_WIN;
        } else if (dealerValue > playerValue) {
            return GameStatus.DEALER_WIN;
        } else {
            return GameStatus.TIE;
        }
    }

    private void validateGameIsActive() {
        if (status.isFinished()) {
            throw new IllegalStateException("Game is already finished with status: " + status.getDisplayName());
        }
    }

    private void emitGameFinishedEvent() {
        if (status.isFinished()) {
            domainEvents.add(GameFinishedEvent.create(
                    this.id,
                    this.playerId,
                    this.status
            ));
        }
    }


    public GameId getId() {
        return id;
    }

    public PlayerId getPlayerId() {
        return playerId;
    }

    public PlayerName getPlayerName() {
        return playerName;
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public Deck getDeck() {
        return deck;
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<Turn> getTurnHistory() {
        return Collections.unmodifiableList(turnHistory);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Game{" + "id=" + id.value() + ", playerId=" + playerId.value() + ", playerName=" + playerName.value() + ", status=" + status + ", playerHandValue=" + playerHand.calculateValue() + ", dealerHandValue=" + dealerHand.calculateValue() + ", turnCount=" + turnHistory.size() + '}';
    }
}
