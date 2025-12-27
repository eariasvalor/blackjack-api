package com.blackjack.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePlayerRequest(
        @NotBlank(message = "Player name cannot be empty")
        String playerName
) {
}