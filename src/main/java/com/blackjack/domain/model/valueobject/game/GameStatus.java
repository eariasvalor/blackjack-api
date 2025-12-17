package com.blackjack.domain.model.valueobject.game;

public enum GameStatus {

    PLAYING("Game in progress", false),

    PLAYER_WIN("Player wins", true),


    DEALER_WIN("Dealer wins", true),

    TIE("It's a tie", true);

    private final String displayName;
    private final boolean finished;


    GameStatus(String displayName, boolean finished) {
        this.displayName = displayName;
        this.finished = finished;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isActive() {
        return !finished;
    }
}