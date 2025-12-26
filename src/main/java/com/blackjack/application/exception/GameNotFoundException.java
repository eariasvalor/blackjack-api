package com.blackjack.application.exception;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(String gameId) {
        super("Game not found with id: " + gameId);
    }
}
