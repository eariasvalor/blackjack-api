package com.blackjack.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PlayGameRequest(@NotBlank(message = "Action cannot be empty") String action) {
}