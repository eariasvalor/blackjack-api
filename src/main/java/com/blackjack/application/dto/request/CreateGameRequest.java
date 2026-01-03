package com.blackjack.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateGameRequest(
        @NotBlank(message = "Player name cannot be empty")
        String playerName,
        @Min(1) @Max(8)
        Integer numberOfDecks
) {

}
