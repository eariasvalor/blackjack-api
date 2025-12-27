package com.blackjack.application.exception;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(String playerId) {
        super("Player not found with id: " + playerId);
    }
}