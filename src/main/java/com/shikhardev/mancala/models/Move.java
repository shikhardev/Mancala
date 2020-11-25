package com.shikhardev.mancala.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Class represents a Move, and provides validation for the current move.
 */
@Component
public class Move {

    @Getter
    @Setter
    private Player currentPlayer;

    @Getter
    @Setter
    private Pit selectedPit;

    public Move(Player currentPlayer, Pit selectedPit) {
        this.currentPlayer = currentPlayer;
        this.selectedPit = selectedPit;
    }

    /**
     * Current move object is invalid if
     *      (1) current player is not the pit owner
     *      (2) pit type is home
     *      (3) no more stones remain in current pit
     *
     * @return {true, false}
     */
    public boolean isValid(){
        // Move is invalid
            //  * If current player is not the pit owner
            //  * If pit type is home
            //  * If no more stones remain in current pit
        return (currentPlayer.equals(selectedPit.getOwner())) &&
                (selectedPit.getPitType() != PitType.HOME) &&
                (selectedPit.getNumberOfStones() != 0);
    }
}
