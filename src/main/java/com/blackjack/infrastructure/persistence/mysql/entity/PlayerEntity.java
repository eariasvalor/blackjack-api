package com.blackjack.infrastructure.persistence.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("players")
public class PlayerEntity {

    @Id
    private String id;

    @Column("name")
    private String name;

    @Column("games_played")
    private int gamesPlayed;

    @Column("games_won")
    private int gamesWon;

    @Column("games_lost")
    private int gamesLost;

    @Column("games_tied")
    private int gamesTied;

    @Column("win_rate")
    private double winRate;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}