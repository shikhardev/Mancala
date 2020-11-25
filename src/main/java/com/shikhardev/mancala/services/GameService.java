package com.shikhardev.mancala.services;

import com.shikhardev.mancala.dto.GameStatus;
import com.shikhardev.mancala.models.Move;
import com.shikhardev.mancala.models.Pit;
import com.shikhardev.mancala.models.Player;
import com.shikhardev.mancala.models.PlayerID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private BoardService boardService;
    private PlayerService playerService;
    private GameStatus gameStatus;


    @Autowired
    public GameService(BoardService boardService, PlayerService playerService, GameStatus gameStatus) {
        this.boardService = boardService;
        this.playerService = playerService;
        this.gameStatus = gameStatus;
    }


    @Value("#{new Integer ('${P1_HOME_PIT}')}")
    private Integer P1_HOME_PIT;

    @Value("#{new Integer ('${P2_HOME_PIT}')}")
    private Integer P2_HOME_PIT;

    /**
     * Sets up the boards, players and the current GameStatus object for a new game.
     * Assumes that the first move will be by Player 1.
     * @return GameStatus object
     */
    public GameStatus initGame() {
        boardService.initBoard();
        playerService.initPlayers();
        gameStatus.setCurrentBoardStatus(boardService.getBoard());
        gameStatus.setEndGame(false);
        gameStatus.setLegalMove(true);
        gameStatus.setNextPlayer(playerService.getP1());
        gameStatus.setWinner(playerService.getEmptyPlayer());
        return gameStatus;
    }

    /**
     * Called after every executeMove call. Returns true if game has ended; else false.
     */
    public boolean isEndGame() {
        return boardService.arePitsInRangeEmpty(0, P1_HOME_PIT) ||
                boardService.arePitsInRangeEmpty(P1_HOME_PIT + 1, P2_HOME_PIT);
    }

    /**
     * Called only if the game has ended.
     * Collects the remaining stones from the board before checking for the player with higher stone count in their home.
     * @return Player object with higher home-stone count. If the number of stones at both homes are equal,
     * return empty player with ID PlayerID.None.
     */
    public Player getGameWinner() {
        boardService.collectRemainingStones();
        int p1Count = boardService.getPlayerHomeCount(playerService.getP1());
        int p2Count = boardService.getPlayerHomeCount(playerService.getP2());
        return p1Count > p2Count ? playerService.getP1() :
                p2Count > p1Count ? playerService.getP2() :
                        playerService.getEmptyPlayer();
    }

    /**
     * Returns Player object of player 1 if currentPlayer is player 2, and vice versa.
     */
    private Player togglePlayer(Player currentPlayer) {
        return (currentPlayer.getId() == PlayerID.PLAYER_1)?
                playerService.getP2() :
                playerService.getP1();
    }

    /**
     * Performs all the necessary validations and performs all steps necessary to execute a move. This is the entry
     * point for clients to make a move request.
     * @param playerID String {PLAYER_1, PLAYER_2}
     * @param pitID [0, 13]
     * @exception IllegalArgumentException: Exception if player id is mis-specified
     * @exception IndexOutOfBoundsException: If the pit with specified pitID does not exist (yet)
     * @return GameStatus object after executing the current move, if everything is valid
     */
    public GameStatus executeMove(String playerID, int pitID) throws IllegalArgumentException, IndexOutOfBoundsException{
        Player player = playerService.getPlayerById(PlayerID.valueOf(playerID));
        if (player.getId() == PlayerID.None)
            throw new IllegalArgumentException("Invalid Player ID");
        Pit pit = boardService.getPitByID(pitID);
        Move move = new Move(player, pit);
        if (move.isValid()) {
            gameStatus.setLegalMove(true);
            boardService.updateBoardForMove(move);
            gameStatus.setCurrentBoardStatus(boardService.getBoard());
            if (isEndGame()) {
                gameStatus.setEndGame(true);
                gameStatus.setWinner(getGameWinner());
            }
            if (!boardService.doesPlayerContinue())
                gameStatus.setNextPlayer(togglePlayer(gameStatus.getNextPlayer()));
            // Here, getNextPlayer still has the current player,
            // since it has not been updated at the time of access
        }
        else {
            gameStatus.setLegalMove(false);
            // Everything else remains the same
        }
        return gameStatus;
    }
}
