package com.blackjack.infrastructure.persistence.mongodb.mapper;

import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.valueobject.card.Card;
import com.blackjack.domain.model.valueobject.card.Rank;
import com.blackjack.domain.model.valueobject.card.Suit;
import com.blackjack.domain.model.valueobject.game.*;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.turn.Turn;
import com.blackjack.domain.model.valueobject.turn.TurnOwner;
import com.blackjack.domain.model.valueobject.turn.TurnType;
import com.blackjack.infrastructure.persistence.mongodb.document.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameDocumentMapper {

    public GameDocument toDocument(Game game) {
        return GameDocument.builder()
                .id(game.getId().value())
                .playerId(game.getPlayerId().value())
                .playerHand(toHandDocument(game.getPlayerHand()))
                .dealerHand(toHandDocument(game.getDealerHand()))
                .deck(toDeckDocument(game.getDeck()))
                .status(game.getStatus().name())
                .turnHistory(toTurnDocuments(game.getTurnHistory()))
                .createdAt(game.getCreatedAt())
                .updatedAt(game.getUpdatedAt())
                .build();
    }

    public Game toDomain(GameDocument document) {
        GameId gameId = GameId.from(document.getId());
        PlayerId playerId = PlayerId.from(document.getPlayerId());
        Hand playerHand = toHand(document.getPlayerHand());
        Hand dealerHand = toHand(document.getDealerHand());
        Deck deck = toDeck(document.getDeck());
        GameStatus status = GameStatus.valueOf(document.getStatus());
        List<Turn> turnHistory = toTurns(document.getTurnHistory());

        return Game.reconstitute(
                gameId,
                playerId,
                playerHand,
                dealerHand,
                deck,
                status,
                turnHistory,
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }


    private HandDocument toHandDocument(Hand hand) {
        List<CardDocument> cardDocs = hand.getCards().stream()
                .map(this::toCardDocument)
                .collect(Collectors.toList());

        return HandDocument.builder()
                .cards(cardDocs)
                .build();
    }

    private Hand toHand(HandDocument document) {
        List<Card> cards = document.getCards().stream()
                .map(this::toCard)
                .collect(Collectors.toList());

        return Hand.from(cards);
    }

    private CardDocument toCardDocument(Card card) {
        return CardDocument.builder()
                .rank(card.rank().name())
                .suit(card.suit().name())
                .build();
    }

    private Card toCard(CardDocument document) {
        Rank rank = Rank.valueOf(document.getRank());
        Suit suit = Suit.valueOf(document.getSuit());
        return new Card(rank, suit);
    }

    private DeckDocument toDeckDocument(Deck deck) {
        List<CardDocument> cardDocs = deck.getCards().stream()
                .map(this::toCardDocument)
                .collect(Collectors.toList());

        return DeckDocument.builder()
                .cards(cardDocs)
                .currentIndex(deck.getCurrentIndex())
                .build();
    }

    private Deck toDeck(DeckDocument document) {
        List<Card> cards = document.getCards().stream()
                .map(this::toCard)
                .collect(Collectors.toList());

        return Deck.reconstitute(cards, document.getCurrentIndex());
    }

    private List<TurnDocument> toTurnDocuments(List<Turn> turns) {
        return turns.stream()
                .map(this::toTurnDocument)
                .collect(Collectors.toList());
    }

    private TurnDocument toTurnDocument(Turn turn) {
        return TurnDocument.builder()
                .turnNumber(turn.turnNumber())
                .type(turn.type().name())
                .owner(turn.owner().name())
                .cardDrawn(turn.hasCard() ? toCardDocument(turn.cardDrawn()) : null)
                .handValue(turn.handValue())
                .timestamp(turn.timestamp())
                .build();
    }

    private List<Turn> toTurns(List<TurnDocument> documents) {
        return documents.stream()
                .map(this::toTurn)
                .collect(Collectors.toList());
    }

    private Turn toTurn(TurnDocument document) {
        Card card = document.getCardDrawn() != null
                ? toCard(document.getCardDrawn())
                : null;

        return new Turn(
                document.getTurnNumber(),
                TurnType.valueOf(document.getType()),
                TurnOwner.valueOf(document.getOwner()),
                card,
                document.getHandValue(),
                document.getTimestamp()
        );
    }
}