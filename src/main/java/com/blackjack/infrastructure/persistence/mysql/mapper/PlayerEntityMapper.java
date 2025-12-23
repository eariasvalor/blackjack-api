package com.blackjack.infrastructure.persistence.mysql.mapper;

import com.blackjack.domain.model.aggregate.Player;
import com.blackjack.domain.model.valueobject.player.PlayerId;
import com.blackjack.domain.model.valueobject.player.PlayerName;
import com.blackjack.infrastructure.persistence.mysql.entity.PlayerEntity;
import org.springframework.stereotype.Component;

@Component
public class PlayerEntityMapper {

    public PlayerEntity toEntity(Player player) {
        return PlayerEntity.builder()
                .id(player.getId().value())
                .name(player.getName().value())
                .gamesPlayed(player.getGamesPlayed())
                .gamesWon(player.getGamesWon())
                .gamesLost(player.getGamesLost())
                .gamesTied(player.getGamesTied())
                .winRate(player.getWinRate())
                .createdAt(player.getCreatedAt())
                .updatedAt(player.getUpdatedAt())
                .build();
    }

    public Player toDomain(PlayerEntity entity) {
        PlayerId playerId = PlayerId.from(entity.getId());
        PlayerName playerName = new PlayerName(entity.getName());

        return Player.reconstitute(
                playerId,
                playerName,
                entity.getGamesPlayed(),
                entity.getGamesWon(),
                entity.getGamesLost(),
                entity.getGamesTied(),
                entity.getWinRate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
