package com.blackjack.infrastructure.persistence.mongodb.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnDocument {

    private int turnNumber;
    private String type;
    private String owner;
    private CardDocument cardDrawn;
    private int handValue;
    private LocalDateTime timestamp;
}