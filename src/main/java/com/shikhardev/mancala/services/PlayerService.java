package com.shikhardev.mancala.services;

import com.shikhardev.mancala.models.Player;
import com.shikhardev.mancala.models.PlayerID;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implements services for Player class, including initialization, getting empty player, and fetching Player objects.
 */
@Service
public class PlayerService {

    @Getter
    private Player p1;
    @Value("#{new Integer ('${P1_HOME_PIT}')}")
    private Integer P1_HOME_PIT;

    @Getter
    private Player p2;
    @Value("#{new Integer ('${P2_HOME_PIT}')}")
    private Integer P2_HOME_PIT;

    public void initPlayers() {
        p1 = new Player(PlayerID.PLAYER_1, P1_HOME_PIT);
        p2 = new Player(PlayerID.PLAYER_2, P2_HOME_PIT);
    }

    /**
     * Returns a conceptual empty player, with ID as PlayerID.None and homeID as -1
     */
    public Player getEmptyPlayer() {
        return new Player(PlayerID.None, -1);
    }

    public Player getPlayerById(PlayerID id) {
        switch (id) {
            case PLAYER_1:
                return p1;
            case PLAYER_2:
                return p2;
        }
        return getEmptyPlayer();
    }
}
