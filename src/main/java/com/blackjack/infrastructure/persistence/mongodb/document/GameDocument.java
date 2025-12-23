package com.blackjack.infrastructure.persistence.mongodb.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class GameDocument {

    @Id
    private String id;

    private String playerId;

    private HandDocument playerHand;
    private HandDocument dealerHand;
    private DeckDocument deck;
    private String status;

    private List<TurnDocument> turnHistory;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
