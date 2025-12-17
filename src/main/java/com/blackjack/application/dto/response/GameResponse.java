package com.blackjack.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;


public record GameResponse(
        @JsonProperty("gameId")
        String gameId,

        @JsonProperty("playerId")
        String playerId,

        @JsonProperty("playerName")
        String playerName,

        @JsonProperty("playerHand")
        List<String> playerHand,

        @JsonProperty("dealerVisibleCards")
        List<String> dealerVisibleCards,

        @JsonProperty("playerHandValue")
        int playerHandValue,

        @JsonProperty("dealerVisibleValue")
        int dealerVisibleValue,

        @JsonProperty("status")
        String status,

        @JsonProperty("turnHistory")
        List<TurnResponse> turnHistory,

        @JsonProperty("createdAt")
        LocalDateTime createdAt,

        @JsonProperty("updatedAt")
        LocalDateTime updatedAt
) {
}
