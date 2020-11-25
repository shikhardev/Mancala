package com.shikhardev.mancala.services;

import com.shikhardev.mancala.models.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents services associated with the board, including initiation, move implementation on the board with
 * stone dynamics handled.
 */
@Service
public class BoardService {

    @Getter
    private Board board;
    private PlayerService playerService;

    @Value("#{new Integer ('${PITS_PER_PLAYER}')}")
    private Integer PITS_PER_PLAYER;

    @Value("#{new Integer ('${STARTING_STONE_COUNT}')}")
    private Integer STARTING_STONE_COUNT;

    @Value("#{new Integer ('${TOTAL_PIT_COUNT}')}")
    private Integer TOTAL_PIT_COUNT;

    @Value("#{new Integer ('${P1_HOME_PIT}')}")
    private Integer P1_HOME_PIT;

    @Value("#{new Integer ('${P2_HOME_PIT}')}")
    private Integer P2_HOME_PIT;

    private boolean playerContinues = false;

    @Autowired
    public BoardService(Board board, PlayerService playerService) {
        this.board = board;
        this.playerService = playerService;
    }

    /**
     * Gets pit if id is valid and board initiated.
     * @param id [0, 13]
     * @return Pit object
     * @throws IndexOutOfBoundsException: if id is invalid
     */
    public Pit getPitByID (int id) throws IndexOutOfBoundsException{
        try {
            return board.getPit(id);
        }
        catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(String.format("ID %d is invalid ID or board uninitiated", id));
        }


    }

    /**
     * Creates a pit object for the specified pitID.
     * Figures out the right
     *      - pit owner {Player object representing player 1 / 2}
     *      - pit type {PitType.PLAYGROUND / PitType.HOME}
     *      - initial stone count {0, 6}
     *      based on the pit id.
     * @param pitID [0, 14]
     * @return Pit object with the right settings.
     */
    public Pit initPit(int pitID) {
        Pit res = new Pit();
        res.setId(pitID);
        if (pitID <= P1_HOME_PIT) {
            res.setOwner(new Player(PlayerID.PLAYER_1, P1_HOME_PIT));
            if (pitID < P1_HOME_PIT) {
                res.setNumberOfStones(STARTING_STONE_COUNT);
                res.setPitType(PitType.PLAYGROUND);
            }
            else {
                res.setNumberOfStones(0);
                res.setPitType(PitType.HOME);
            }
        }
        else {
            res.setOwner(new Player(PlayerID.PLAYER_2, P2_HOME_PIT));
            if (pitID < P2_HOME_PIT) {
                res.setNumberOfStones(STARTING_STONE_COUNT);
                res.setPitType(PitType.PLAYGROUND);
            }
            else {
                res.setNumberOfStones(0);
                res.setPitType(PitType.HOME);
            }
        }
        return res;
    }

    /**
     * Initializes the class' board object with all 14 pits
     */
    public void initBoard(){
        List<Pit> temp_board = new ArrayList<>();
        for (int i = 0; i < TOTAL_PIT_COUNT; i++)
            temp_board.add(initPit(i));
        board.setAllPits(temp_board);
    }

    /**
     * Returns true if all pits in the specified range have 0 stones. Else, returns false.
     */
    public boolean arePitsInRangeEmpty (int fromID, int toID) {
        for (int i = fromID; i < toID; i ++)
            if (board.getPit(i).getNumberOfStones() != 0)
                return false;
        return true;
    }

    /**
     * This method is typically called after execution of a move.
     * Returns true if the same player should make the next move also. Else, returns false.
     */
    public boolean doesPlayerContinue() {
        // playerContinues is dynamically set during the execution of a move in updateBoardForMove method
        return playerContinues;
    }

    /**
     * Returns the number of stones at the Home pit of the specified player
     * @param player: Player object whose home-pit count is requested
     * @exception IllegalArgumentException: if the specified player is neither player 1 nor player 2.
     */
    public int getPlayerHomeCount(Player player) {
        int homeID = player.getHomeID();
        if (homeID == -1)
            throw new IllegalArgumentException("Homeless player.");
        Pit home = board.getPit(homeID);
        return home.getNumberOfStones();
    }

    /**
     * If the game has ended, at least one side of the board is empty.
     * If there are remaining stones on one of the sides, it will be allocated to player of that side.
     */
    public void collectRemainingStones() {
        collectRemainingStones(0, P1_HOME_PIT);
        collectRemainingStones(P1_HOME_PIT + 1, P2_HOME_PIT);
    }

    /**
     * Only called from the collectRemainingStones() function.
     * Increments the home stone count by the total number of remaining stones in the board.
     * @param lower: Starting pit ID of the current side
     * @param upper: Home ID of the current side
     */
    private void collectRemainingStones(int lower, int upper) {
        int count = 0;
        for (int i = lower; i < upper; i ++)
            count += board.getPit(i).getNumberOfStones();
        board.getPit(upper).incrementStoneCountBy(count);
    }

    /**
     * While moving, if the move ends on a pit with 0 stones, the current player captures the stones of the opposite
     * side. Home stone count increases by oppositeStoneCount + 1 (player's latest stone).
     * @param mover: Player object of the current move-maker
     * @param latestPit: Pit object at which the current move ended
     */
    public void captureOppositeStones(Player mover, Pit latestPit) {
        if (latestPit.getNumberOfStones() != 1)
            return;
        int homeLowerBound = mover.getHomeID().equals(P1_HOME_PIT) ? 0 : P1_HOME_PIT + 1;
        int homeUpperBound = mover.getHomeID().equals(P1_HOME_PIT) ? P1_HOME_PIT : P2_HOME_PIT;
        int oppositeID = TOTAL_PIT_COUNT - latestPit.getId() - 2;   // 2 for the 2 homes

        // If oppositeID falls in the home side
        // or if number of stones in oppositeID == 0
        // nothing to collect
        if (((oppositeID >= homeLowerBound) && (oppositeID < homeUpperBound)) ||
                (board.getPit(oppositeID).getNumberOfStones() == 0))
            return;

        int stonesToCapture = board.getPit(oppositeID).getNumberOfStones() +
                latestPit.getNumberOfStones();
        board.getPit(oppositeID).setNumberOfStones(0);
        board.getPit(latestPit.getId()).setNumberOfStones(0);
        board.getPit(mover.getHomeID()).incrementStoneCountBy(stonesToCapture);
    }

    /**
     * Executes the specified Move object. Assumes move has already been validated.
     * Calls the captureOppositeStones as well as updates the playerContinues variable to denote if the next move is
     * to be made by a different player.
     * @param move: Move object
     */
    public void updateBoardForMove (Move move) {
        Pit startingPit = move.getSelectedPit();
        int nextPitID = startingPit.getId();
        int currentStoneCount = startingPit.getNumberOfStones();
        Pit nextPit = null; // currentStoneCount is always > 0. Null instance is always change.

        while (currentStoneCount > 0) {
            nextPitID = (nextPitID + 1) % TOTAL_PIT_COUNT;
            nextPit = board.getPit(nextPitID);

            // If next pit is playground or home of the current mover, increment their stone count
            // Correspondingly decrement from current pit
            if ((nextPit.getPitType() == PitType.PLAYGROUND) ||
                    (nextPit.getOwner().equals(move.getCurrentPlayer()))) {
                nextPit.incrementStoneCountBy(1);
                board.getPit(startingPit.getId()).decrementStoneCountBy(1);
                currentStoneCount--;
            }
        }
        assert nextPit != null;
        if (nextPit.getPitType() == PitType.PLAYGROUND) {
            captureOppositeStones(move.getCurrentPlayer(), nextPit);
            playerContinues = false;
        }
        // If not in playground, nextPit has to be home of the mover
        // Since it terminates at home, it gets the chance again
        else
            playerContinues = true;

    }


}
