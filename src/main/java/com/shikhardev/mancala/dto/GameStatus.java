package com.shikhardev.mancala.dto;

import com.shikhardev.mancala.models.Board;
import com.shikhardev.mancala.models.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Class of objects that are used to describe the current status of the game.
 * An object of this class will always exist when the game us running.
 */
@Component
public class GameStatus {

    @Getter
    @Setter
    private Board currentBoardStatus;   // Board status after the last move

    @Getter
    @Setter
    private boolean isLegalMove;    // Was the last made move legal

    @Getter
    @Setter
    private Player nextPlayer;   // Which player has the control for the next move

    @Getter
    @Setter
    private boolean isEndGame;  // Has the game ended?

    @Getter
    @Setter
    private Player winner;      // Point to the winner if the game has ended

}
