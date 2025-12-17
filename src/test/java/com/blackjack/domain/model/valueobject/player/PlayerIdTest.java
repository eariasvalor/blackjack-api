package com.blackjack.domain.model.valueobject.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PlayerId Value Object Tests")
class PlayerIdTest {

    @Test
    @DisplayName("Should generate unique PlayerId")
    void shouldGenerateUniquePlayerId() {
        PlayerId id1 = PlayerId.generate();
        PlayerId id2 = PlayerId.generate();

        assertThat(id1).isNotNull();
        assertThat(id2).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.value()).startsWith("player-");
    }

    @Test
    @DisplayName("Should create PlayerId from string")
    void shouldCreatePlayerIdFromString() {
        String value = "player-123";

        PlayerId playerId = PlayerId.from(value);

        assertThat(playerId.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> new PlayerId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Player ID cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when value is empty")
    void shouldThrowExceptionWhenValueIsEmpty() {
        assertThatThrownBy(() -> new PlayerId(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player ID cannot be empty");
    }

    @Test
    @DisplayName("Two PlayerIds with same value should be equal")
    void shouldBeEqualWhenSameValue() {
        PlayerId id1 = PlayerId.from("player-123");
        PlayerId id2 = PlayerId.from("player-123");

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}