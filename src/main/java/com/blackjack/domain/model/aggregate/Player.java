package com.blackjack.domain.model.aggregate;

import com.blackjack.domain.model.valueobject.game.GameStatus;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;

import java.time.LocalDateTime;
import java.util.Objects;


public class Player {

    private final PlayerId id;

    private PlayerName name;

    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int gamesTied;
    private double winRate;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Player(PlayerId id, PlayerName name,
                   int gamesPlayed, int gamesWon, int gamesLost, int gamesTied, double winRate,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "PlayerId cannot be null");
        this.name = Objects.requireNonNull(name, "PlayerName cannot be null");

        if (gamesPlayed < 0) {
            throw new IllegalArgumentException("Games played cannot be negative");
        }
        if (gamesWon < 0) {
            throw new IllegalArgumentException("Games won cannot be negative");
        }
        if (gamesLost < 0) {
            throw new IllegalArgumentException("Games lost cannot be negative");
        }
        if (gamesTied < 0) {
            throw new IllegalArgumentException("Games tied cannot be negative");
        }
        if (winRate < 0.0 || winRate > 100.0) {
            throw new IllegalArgumentException("Win rate must be between 0 and 100");
        }

        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.gamesTied = gamesTied;
        this.winRate = winRate;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");
    }

    public static Player create(PlayerName name) {
        PlayerId id = PlayerId.generate();
        LocalDateTime now = LocalDateTime.now();

        return new Player(
                id,
                name,
                0,
                0,
                0,
                0,
                0.0,
                now,
                now
        );
    }

    public static Player reconstitute(PlayerId id, PlayerName name,
                                      int gamesPlayed, int gamesWon, int gamesLost, int gamesTied,
                                      double winRate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Player(
                id, name,
                gamesPlayed, gamesWon, gamesLost, gamesTied, winRate,
                createdAt, updatedAt
        );
    }

    public void updateName(PlayerName newName) {
        Objects.requireNonNull(newName, "New name cannot be null");
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordGameResult(GameStatus gameStatus) {
        Objects.requireNonNull(gameStatus, "Game status cannot be null");

        if (!gameStatus.isFinished()) {
            throw new IllegalArgumentException(
                    "Cannot record result for game still in progress: " + gameStatus
            );
        }

        this.gamesPlayed++;

        switch (gameStatus) {
            case PLAYER_WIN -> this.gamesWon++;
            case DEALER_WIN -> this.gamesLost++;
            case TIE -> this.gamesTied++;
            default -> throw new IllegalStateException(
                    "Unexpected game status: " + gameStatus
            );
        }

        recalculateWinRate();

        this.updatedAt = LocalDateTime.now();
    }

    private void recalculateWinRate() {
        if (gamesPlayed == 0) {
            this.winRate = 0.0;
        } else {
            this.winRate = ((double) gamesWon / gamesPlayed) * 100.0;
        }
    }


    public PlayerId getId() {
        return id;
    }

    public PlayerName getName() {
        return name;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public int getGamesTied() {
        return gamesTied;
    }

    public double getWinRate() {
        return winRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ========== Object Methods ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id.value() +
                ", name=" + name.value() +
                ", gamesPlayed=" + gamesPlayed +
                ", gamesWon=" + gamesWon +
                ", gamesLost=" + gamesLost +
                ", gamesTied=" + gamesTied +
                ", winRate=" + String.format("%.2f", winRate) + "%" +
                '}';
    }
}