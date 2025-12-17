package com.blackjack.domain.model.valueobject.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GameId Value Object Tests")
class GameIdTest {

    @Test
    @DisplayName("Should generate unique GameId")
    void shouldGenerateUniqueGameId() {
        GameId id1 = GameId.generate();
        GameId id2 = GameId.generate();

        assertThat(id1).isNotNull();
        assertThat(id2).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.value()).startsWith("game-");
    }

    @Test
    @DisplayName("Should create GameId from string")
    void shouldCreateGameIdFromString() {
        String value = "game-123";

        GameId gameId = GameId.from(value);

        assertThat(gameId.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> new GameId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Game ID cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when value is empty")
    void shouldThrowExceptionWhenValueIsEmpty() {
        assertThatThrownBy(() -> new GameId(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Game ID cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThatThrownBy(() -> new GameId("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Game ID cannot be empty");
    }

    @Test
    @DisplayName("Two GameIds with same value should be equal")
    void shouldBeEqualWhenSameValue() {
        GameId id1 = GameId.from("game-123");
        GameId id2 = GameId.from("game-123");

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}