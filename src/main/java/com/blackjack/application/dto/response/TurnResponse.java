package com.blackjack.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record TurnResponse(
        @JsonProperty("turnNumber")
        int turnNumber,

        @JsonProperty("type")
        String type,

        @JsonProperty("owner")
        String owner,

        @JsonProperty("cardDrawn")
        String cardDrawn,

        @JsonProperty("handValue")
        int handValue,

        @JsonProperty("timestamp")
        LocalDateTime timestamp
) {
}
