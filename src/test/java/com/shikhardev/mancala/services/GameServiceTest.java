package com.shikhardev.mancala.services;

import com.shikhardev.mancala.dto.GameStatus;
import com.shikhardev.mancala.models.Pit;
import com.shikhardev.mancala.models.PlayerID;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class GameServiceTest {

    @BeforeEach
    void setUp() {
        gameService.initGame();
    }

    @Autowired
    BoardService boardService;

    @Autowired
    PlayerService playerService;

    @Autowired
    GameService gameService;

    @Autowired
    GameStatus gameStatus;

    @Test
    @DisplayName("Verify if all pits have been appropriately created after init")
    void isBoardCreated() {
        Assert.assertEquals(14, boardService.getBoard().getAllPits().size());
        int totalExpectedStones = 6 * 12;
        int sum = boardService.
                getBoard()
                .getAllPits()
                .stream()
                .mapToInt(Pit::getNumberOfStones)
                .sum();
        Assert.assertEquals(sum, totalExpectedStones);
    }

    @Test
    @DisplayName("Verify if all players have been appropriately created after init")
    void arePlayersCreated() {
        Assert.assertNotNull(playerService.getP1());
        Assert.assertNotNull(playerService.getP2());
    }

    @Test
    @DisplayName("Verify if gameStatus reflects a newly created game after init")
    void verifyInitGameStatus() {
        int totalExpectedStones = 6 * 12;
        int sum;

        // Verifying gameStatus
        sum = gameStatus.getCurrentBoardStatus()
                .getAllPits()
                .stream()
                .mapToInt(Pit::getNumberOfStones)
                .sum();
        Assert.assertEquals(sum, totalExpectedStones);
        Assert.assertFalse(gameStatus.isEndGame());
        Assert.assertTrue(gameStatus.isLegalMove());
        Assert.assertSame(gameStatus.getNextPlayer().getId(), PlayerID.PLAYER_1);
        Assert.assertSame(gameStatus.getWinner().getId(), PlayerID.None);
    }

    void setStoneCountForPitsInRange (int stoneCount, int lowerBound, int upperBound) {
        for (int i = lowerBound; i < upperBound; i ++)
            boardService.getPitByID(i).setNumberOfStones(stoneCount);
    }

    @Test
    @DisplayName("isEndGame should return false if neither side is completely empty")
    void partlyFilledSide_endGameStatus() {
        // If a side is partially empty, game has not ended
        setStoneCountForPitsInRange(0, 0, 4);
        GameStatus res = gameService.executeMove("PLAYER_1", 5);
        Assert.assertFalse(res.isEndGame());
        // No winner yet
        Assert.assertEquals(PlayerID.None, gameStatus.getWinner().getId());
    }

    @Test
    @DisplayName("isEndGame should return true if at least one side is completely empty")
    void oneSideEmpty_endGameStatus() {
        setStoneCountForPitsInRange(0, 0, 5);
        setStoneCountForPitsInRange(1, 5, 6);
        GameStatus res = gameService.executeMove("PLAYER_1", 5);
        Assert.assertTrue(res.isEndGame());
        // P1 home stone count is 0 and P2 side of board is full
        // So, by collecting at the end, P2 should be winner
        Assert.assertEquals(res.getWinner(), playerService.getP2());

        gameService.initGame();
        setStoneCountForPitsInRange(0, 7, 12);
        setStoneCountForPitsInRange(1, 12, 13);
        res = gameService.executeMove("PLAYER_2", 12);
        Assert.assertTrue(res.isEndGame());
        Assert.assertEquals(res.getWinner(), playerService.getP1());
    }

    @Test
    @DisplayName("isEndGame should return true but winner should be emptyPlayer for draw")
    void verifyDrawGameStatus() {
        setStoneCountForPitsInRange(0, 0, 6);
        setStoneCountForPitsInRange(1, 6, 7);
        setStoneCountForPitsInRange(0, 7, 12);
        setStoneCountForPitsInRange(1, 12, 13);
        GameStatus res = gameService.executeMove("PLAYER_2", 12);
        Assert.assertTrue(res.isEndGame());
        Assert.assertEquals(PlayerID.None, res.getWinner().getId());
    }

    @Test
    @DisplayName("ExecuteMove for valid parameters")
    void executeMove() {
        // move.isValid() has been tested in models.MoveTest
        // updateBoardForMove() has been tested in services.BoardServiceTest
        // isEndGame() has been tested in services.BoardServiceTest
        // doesPlayerContinue()  has been tested in services.BoardServiceTest
        // => While of this function has been tested before
    }

    @Test
    @DisplayName("Simulate ExecuteMove for illegal player id")
    void testIllegalPlayerIDForExecuteMove() {
        assertThrows(IllegalArgumentException.class, () -> gameService.executeMove("Shikhar", 0));
    }

    @Test
    @DisplayName("Simulate ExecuteMove for illegal pit id")
    void testIllegalPitIDForExecuteMove() {
        assertThrows(IndexOutOfBoundsException.class, () -> gameService.executeMove("PLAYER_1", 100));
    }

}