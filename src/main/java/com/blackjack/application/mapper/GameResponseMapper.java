package com.blackjack.application.mapper;

import com.blackjack.application.dto.response.GameResponse;
import com.blackjack.application.dto.response.TurnResponse;
import com.blackjack.domain.model.aggregate.Game;
import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.card.Card;
import com.blackjack.domain.model.valueobject.turn.Turn;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameResponseMapper {

    public GameResponse toResponse(Game game, Player player) {
        return new GameResponse(
                game.getId().value(),
                game.getPlayerId().value(),
                player.getName().value(),
                mapCards(game.getPlayerHand().getCards()),
                mapDealerVisibleCards(game),
                game.getPlayerHand().calculateValue(),
                calculateDealerVisibleValue(game),
                game.getStatus().name(),
                mapTurns(game.getTurnHistory()),
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }

    private List<String> mapCards(List<Card> cards) {
        return cards.stream()
                .map(Card::getSymbol)
                .collect(Collectors.toList());
    }

    private List<String> mapDealerVisibleCards(Game game) {
        if (game.getStatus().isFinished()) {
            return mapCards(game.getDealerHand().getCards());
        } else {
            Card firstCard = game.getDealerHand().getFirstCard();
            return firstCard != null ? List.of(firstCard.getSymbol()) : List.of();
        }
    }

    private int calculateDealerVisibleValue(Game game) {
        if (game.getStatus().isFinished()) {
            return game.getDealerHand().calculateValue();
        } else {
            Card firstCard = game.getDealerHand().getFirstCard();
            return firstCard != null ? firstCard.getValue() : 0;
        }
    }

    private List<TurnResponse> mapTurns(List<Turn> turns) {
        return turns.stream()
                .map(this::mapTurn)
                .collect(Collectors.toList());
    }

    private TurnResponse mapTurn(Turn turn) {
        return new TurnResponse(
                turn.turnNumber(),
                turn.type().name(),
                turn.owner().name(),
                turn.hasCard() ? turn.cardDrawn().getSymbol() : null,
                turn.handValue(),
                turn.timestamp()
        );
    }
}