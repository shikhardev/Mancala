package com.shikhardev.mancala.controllers;

import com.shikhardev.mancala.dto.GameStatus;
import com.shikhardev.mancala.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class that provides the URL entry point to the game
 */
@RestController
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * Entry point to the game
     * @return GameStatus object that describes the current game status
     */
    @RequestMapping("/start")
    public GameStatus start(){
        return gameService.initGame();
    }

    /**
     * Request route interfacing web-client and playing the game
     * @param playerID {PLAYER_1, PLAYER_2}: Which player is playing the current move?
     * @param pitID [0 - 13]: For the specified player, what pit are we trying to play?
     * @return GameStatus object that describes the current game status
     */
    @RequestMapping("/move")
    public GameStatus move(@RequestParam("playerID") String playerID,
                           @RequestParam("pitID") Integer pitID) {
        return gameService.executeMove(playerID, pitID);
    }
}
//  /move?playerID=12&pitID=0