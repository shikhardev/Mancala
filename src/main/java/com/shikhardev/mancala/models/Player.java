package com.shikhardev.mancala.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Class represents a player.
 * Player information includes
 *         (1) player id
 *         (2) player's home id (6 for player 1 and 13 for player 2)
 * Implements getters / setters for each of the fields and overrides the equal method to allow comparison.
 */
@Component
public class Player {

    @Getter
    @Setter
    private PlayerID id;

    @Getter
    @Setter
    private Integer homeID;

    public Player() {
        this.id = PlayerID.None;
        this.homeID = -1;
    }

    public Player(PlayerID id, Integer homeID) {
        this.id = id;
        this.homeID = homeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id &&
                homeID.equals(player.homeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, homeID);
    }
}
