package com.blackjack.application.dto.response;

public record PlayerRankingResponse(
        String playerId,
        String playerName,
        int gamesPlayed,
        int gamesWon,
        double winRate
) {
}