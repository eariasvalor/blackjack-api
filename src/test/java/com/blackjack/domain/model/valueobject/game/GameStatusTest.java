package com.blackjack.domain.model.valueobject.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GameStatus Enum Tests")
class GameStatusTest {

    @Test
    @DisplayName("PLAYING should not be finished")
    void playingShouldNotBeFinished() {
        GameStatus status = GameStatus.PLAYING;

        assertThat(status.isFinished()).isFalse();
        assertThat(status.isActive()).isTrue();
    }

    @Test
    @DisplayName("PLAYER_WIN should be finished")
    void playerWinShouldBeFinished() {
        GameStatus status = GameStatus.PLAYER_WIN;

        assertThat(status.isFinished()).isTrue();
        assertThat(status.isActive()).isFalse();
        assertThat(status.getDisplayName()).isEqualTo("Player wins");
    }

    @Test
    @DisplayName("DEALER_WIN should be finished")
    void dealerWinShouldBeFinished() {
        GameStatus status = GameStatus.DEALER_WIN;

        assertThat(status.isFinished()).isTrue();
        assertThat(status.isActive()).isFalse();
        assertThat(status.getDisplayName()).isEqualTo("Dealer wins");
    }

    @Test
    @DisplayName("TIE should be finished")
    void tieShouldBeFinished() {
        GameStatus status = GameStatus.TIE;

        assertThat(status.isFinished()).isTrue();
        assertThat(status.isActive()).isFalse();
        assertThat(status.getDisplayName()).isEqualTo("It's a tie");
    }

    @Test
    @DisplayName("Should have exactly 4 statuses")
    void shouldHaveExactly4Statuses() {
        GameStatus[] statuses = GameStatus.values();

        assertThat(statuses).hasSize(4);
    }

    @Test
    @DisplayName("Should be able to get status by name")
    void shouldGetStatusByName() {
        GameStatus status = GameStatus.valueOf("PLAYING");

        assertThat(status).isEqualTo(GameStatus.PLAYING);
    }

    @Test
    @DisplayName("Display names should be meaningful")
    void displayNamesShouldBeMeaningful() {
        assertThat(GameStatus.PLAYING.getDisplayName()).isEqualTo("Game in progress");
        assertThat(GameStatus.PLAYER_WIN.getDisplayName()).isEqualTo("Player wins");
        assertThat(GameStatus.DEALER_WIN.getDisplayName()).isEqualTo("Dealer wins");
        assertThat(GameStatus.TIE.getDisplayName()).isEqualTo("It's a tie");
    }
}