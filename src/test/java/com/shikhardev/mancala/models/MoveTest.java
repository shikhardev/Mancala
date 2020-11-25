package com.shikhardev.mancala.models;

import com.shikhardev.mancala.dto.GameStatus;
import com.shikhardev.mancala.services.GameService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class MoveTest {

    @Autowired
    GameService gameService;

    @Test
    void isValid() {
        gameService.initGame();
        // Move an invalid ID will not happen, because invalid pit cannot be instantiated

        // Move from home
        GameStatus res = gameService.executeMove("PLAYER_1", 6);
        Assert.assertFalse(res.isLegalMove());

        // Move from opponent
        res = gameService.executeMove("PLAYER_1", 10);
        Assert.assertFalse(res.isLegalMove());

        // Right move
        res = gameService.executeMove("PLAYER_1", 0);
        Assert.assertTrue(res.isLegalMove());
    }
}