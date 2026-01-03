package com.blackjack.domain.model.aggregate;

import com.blackjack.domain.model.aggregate.mother.GameMother;
import com.blackjack.domain.model.valueobject.game.DeckCount;
import com.blackjack.domain.model.valueobject.game.GameStatus;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.turn.Turn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Game Aggregate Tests")
class GameTest {

    @Test
    @DisplayName("Should create game with initial deal")
    void shouldCreateGameWithInitialDeal() {
        PlayerId playerId = PlayerId.generate();

        Game game = GameMother.withPlayer(playerId);

        assertThat(game).isNotNull();
        assertThat(game.getId()).isNotNull();
        assertThat(game.getPlayerId()).isEqualTo(playerId);
        assertThat(game.getStatus()).isEqualTo(GameStatus.PLAYING);
        assertThat(game.getCreatedAt()).isNotNull();
        assertThat(game.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should deal two cards to player and one to dealer")
    void shouldDealTwoCardsToPlayerAndOneToDealer() {
        Game game = GameMother.defaultGame();

        assertThat(game.getPlayerHand().size()).isEqualTo(2);
        assertThat(game.getDealerHand().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should have empty turn history initially")
    void shouldHaveEmptyTurnHistoryInitially() {
        Game game = GameMother.defaultGame();

        assertThat(game.getTurnHistory()).isEmpty();
    }

    @Test
    @DisplayName("Should have deck with 49 cards after initial deal")
    void shouldHaveDeckWith49CardsAfterInitialDeal() {
        Game game = GameMother.defaultGame();

        assertThat(game.getDeck().remainingCards()).isEqualTo(49);
    }

    @Test
    @DisplayName("Should allow player to hit")
    void shouldAllowPlayerToHit() {
        Game game = GameMother.defaultGame();
        int initialHandSize = game.getPlayerHand().size();

        Turn turn = game.hit();

        assertThat(turn).isNotNull();
        assertThat(game.getPlayerHand().size()).isEqualTo(initialHandSize + 1);
        assertThat(game.getTurnHistory()).hasSize(1);
        assertThat(turn.cardDrawn()).isNotNull();
    }

    @Test
    @DisplayName("Should update game status to DEALER_WIN when player busts")
    void shouldUpdateStatusToDealerWinWhenPlayerBusts() {
        Game game = GameMother.wherePlayerWillBust();

        game.hit();

        assertThat(game.getPlayerHand().isBusted()).isTrue();
        assertThat(game.getStatus()).isEqualTo(GameStatus.DEALER_WIN);
    }

    @Test
    @DisplayName("Should not allow hit when game is finished")
    void shouldNotAllowHitWhenGameIsFinished() {
        Game game = GameMother.wherePlayerWillBust();
        game.hit();
        assertThat(game.getStatus().isFinished()).isTrue();
        assertThatThrownBy(() -> game.hit())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Game is already finished");
    }

    @Test
    @DisplayName("Should allow player to stand")
    void shouldAllowPlayerToStand() {
        Game game = GameMother.defaultGame();

        List<Turn> dealerTurns = game.stand();

        assertThat(dealerTurns).isNotEmpty();
        assertThat(game.getStatus().isFinished()).isTrue();
        assertThat(game.getTurnHistory()).isNotEmpty();
    }

    @Test
    @DisplayName("Should dealer draw until 17 or more")
    void shouldDealerDrawUntil17OrMore() {
        Game game = GameMother.whereDealerWins();

        game.stand();

        assertThat(game.getDealerHand().calculateValue()).isGreaterThanOrEqualTo(17);
    }

    @Test
    @DisplayName("Should player win when dealer busts")
    void shouldPlayerWinWhenDealerBusts() {
        Game game = GameMother.whereDealerWillBust();

        game.stand();

        assertThat(game.getDealerHand().isBusted()).isTrue();
        assertThat(game.getStatus()).isEqualTo(GameStatus.PLAYER_WIN);
    }

    @Test
    @DisplayName("Should player win when higher value than dealer")
    void shouldPlayerWinWhenHigherValue() {
        Game game = GameMother.wherePlayerWins();

        game.stand();

        assertThat(game.getPlayerHand().calculateValue()).isEqualTo(20);
        assertThat(game.getDealerHand().calculateValue()).isEqualTo(18);
        assertThat(game.getStatus()).isEqualTo(GameStatus.PLAYER_WIN);
    }

    @Test
    @DisplayName("Should dealer win when higher value than player")
    void shouldDealerWinWhenHigherValue() {
        Game game = GameMother.whereDealerWins();

        game.stand();

        assertThat(game.getPlayerHand().calculateValue()).isEqualTo(18);
        assertThat(game.getDealerHand().calculateValue()).isEqualTo(20);
        assertThat(game.getStatus()).isEqualTo(GameStatus.DEALER_WIN);
    }

    @Test
    @DisplayName("Should be tie when same value")
    void shouldBeTieWhenSameValue() {
        Game game = GameMother.whereTie();

        game.stand();

        assertThat(game.getPlayerHand().calculateValue()).isEqualTo(19);
        assertThat(game.getDealerHand().calculateValue()).isEqualTo(19);
        assertThat(game.getStatus()).isEqualTo(GameStatus.TIE);
    }

    @Test
    @DisplayName("Should detect blackjack for player")
    void shouldDetectBlackjackForPlayer() {
        Game game = GameMother.wherePlayerHasBlackjack();

        assertThat(game.getPlayerHand().isBlackjack()).isTrue();
    }

    @Test
    @DisplayName("Should create game with 6 decks")
    void shouldCreateGameWith6Decks() {
        PlayerId playerId = PlayerId.generate();
        DeckCount numberOfDecks = DeckCount.of(6);

        Game game = Game.create(playerId, numberOfDecks);

        assertThat(game.getDeck().remainingCards()).isEqualTo(309);
    }
}