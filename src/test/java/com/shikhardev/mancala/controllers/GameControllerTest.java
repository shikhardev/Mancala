package com.shikhardev.mancala.controllers;

import com.shikhardev.mancala.dto.GameStatus;
import com.shikhardev.mancala.models.*;
import com.shikhardev.mancala.services.GameService;
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
class GameControllerTest {

    @Autowired
    GameService gameService;

    @Test
    @DisplayName("Integration test: Starting a new game")
    void start() {
        GameStatus gameStatus = gameService.initGame();
        Assert.assertFalse(gameStatus.isEndGame());
        Assert.assertEquals(PlayerID.None, gameStatus.getWinner().getId());
        Assert.assertTrue(gameStatus.isLegalMove());
        Assert.assertEquals(PlayerID.PLAYER_1, gameStatus.getNextPlayer().getId());

        for (int i = 0; i < 14; i ++) {
            Pit pit = gameStatus.getCurrentBoardStatus().getPit(i);
            if (pit.getPitType() == PitType.PLAYGROUND)
                Assert.assertEquals(6, (int)pit.getNumberOfStones());
            else
                Assert.assertEquals(0, (int)pit.getNumberOfStones());
        }
    }

    @Test
    @DisplayName("Integration test: Making a move")
    void move() {
        GameStatus gameStatus = gameService.initGame();
        gameService.executeMove("PLAYER_1", 0);
        Assert.assertFalse(gameStatus.isEndGame());
        Assert.assertEquals(PlayerID.None, gameStatus.getWinner().getId());
        Assert.assertTrue(gameStatus.isLegalMove());
        Assert.assertEquals(PlayerID.PLAYER_1, gameStatus.getNextPlayer().getId());
        Assert.assertEquals(0, (int)gameStatus.getCurrentBoardStatus().getPit(0).getNumberOfStones());
        Assert.assertEquals(7, (int)gameStatus.getCurrentBoardStatus().getPit(1).getNumberOfStones());
        Assert.assertEquals(1, (int)gameStatus.getCurrentBoardStatus().getPit(6).getNumberOfStones());
    }
}