package com.blackjack.domain.model.valueobject.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PlayerName Value Object Tests")
class PlayerNameTest {

    @Test
    @DisplayName("Should create valid player name")
    void shouldCreateValidPlayerName() {
        String name = "Juan";

        PlayerName playerName = new PlayerName(name);

        assertThat(playerName.value()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should trim whitespace from player name")
    void shouldTrimWhitespace() {
        String nameWithSpaces = "  Juan  ";
        String expectedName = "Juan";

        PlayerName playerName = new PlayerName(nameWithSpaces);

        assertThat(playerName.value()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Two player names with same value should be equal")
    void shouldBeEqualWhenSameValue() {
        PlayerName name1 = new PlayerName("Juan");
        PlayerName name2 = new PlayerName("Juan");

        assertThat(name1).isEqualTo(name2);
        assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
    }

    @Test
    @DisplayName("Two player names after trim should be equal")
    void shouldBeEqualAfterTrim() {
        PlayerName name1 = new PlayerName("  Juan  ");
        PlayerName name2 = new PlayerName("Juan");

        assertThat(name1).isEqualTo(name2);
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNull() {
        assertThatThrownBy(() -> new PlayerName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Player name cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when name is empty")
    void shouldThrowExceptionWhenEmpty() {
        assertThatThrownBy(() -> new PlayerName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player name cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when name is only whitespace")
    void shouldThrowExceptionWhenOnlyWhitespace() {
        assertThatThrownBy(() -> new PlayerName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player name cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when name is too short")
    void shouldThrowExceptionWhenTooShort() {
        assertThatThrownBy(() -> new PlayerName("J"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 2 characters");
    }

    @Test
    @DisplayName("Should throw exception when name is too long")
    void shouldThrowExceptionWhenTooLong() {
        String longName = "A".repeat(51);
        assertThatThrownBy(() -> new PlayerName(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not exceed 50 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan", "Maria_123", "John-Doe", "Player 1", "AB", "Test_User-99"})
    @DisplayName("Should accept valid names")
    void shouldAcceptValidNames(String validName) {
        PlayerName playerName = new PlayerName(validName);
        assertThat(playerName.value()).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan@mail", "María José", "Player<1>", "User!123", "Test#Name", "A$B"})
    @DisplayName("Should reject invalid characters")
    void shouldRejectInvalidCharacters(String invalidName) {
        assertThatThrownBy(() -> new PlayerName(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("can only contain letters, numbers");
    }

    @Test
    @DisplayName("Should accept name with exactly 2 characters")
    void shouldAcceptMinLength() {
        PlayerName playerName = new PlayerName("AB");
        assertThat(playerName.value()).hasSize(2);
    }

    @Test
    @DisplayName("Should accept name with exactly 50 characters")
    void shouldAcceptMaxLength() {
        String maxLengthName = "A".repeat(50);

        PlayerName playerName = new PlayerName(maxLengthName);
        assertThat(playerName.value()).hasSize(50);
    }
}