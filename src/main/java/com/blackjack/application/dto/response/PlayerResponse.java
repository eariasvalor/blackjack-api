package com.blackjack.application.dto.response;

import java.time.LocalDateTime;

public record PlayerResponse(
        String playerId,
        String playerName,
        int gamesPlayed,
        int gamesWon,
        int gamesLost,
        int gamesTied,
        double winRate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}