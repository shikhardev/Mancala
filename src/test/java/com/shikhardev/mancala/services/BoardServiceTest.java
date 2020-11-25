package com.shikhardev.mancala.services;

import com.shikhardev.mancala.models.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class BoardServiceTest {

    @Autowired
    BoardService boardService;

    @Autowired
    PlayerService playerService;

    @BeforeEach
    void setUp() {
        boardService.initBoard();
        playerService.initPlayers();
    }

    @Test
    @DisplayName("Verify properties for Player1 playground pits")
    void p1_playgroundPitVerify() {
        Pit pit = boardService.getPitByID(0);
        Assert.assertEquals(0, (int) pit.getId());
        Assert.assertSame(pit.getOwner().getId(), PlayerID.PLAYER_1);
        Assert.assertSame(pit.getPitType(), PitType.PLAYGROUND);
        Assert.assertEquals(6, (int) pit.getNumberOfStones());
        pit.incrementStoneCountBy(10);
        Assert.assertEquals(16, (int) pit.getNumberOfStones());
        pit.decrementStoneCountBy(10);
        Assert.assertEquals(6, (int) pit.getNumberOfStones());
    }

    @Test
    @DisplayName("Verify properties for Player1 home pit")
    void p1_homePitVerify() {
        Pit pit = boardService.initPit(6);
        Assert.assertEquals(6, (int) pit.getId());
        Assert.assertSame(pit.getOwner().getId(), PlayerID.PLAYER_1);
        Assert.assertSame(pit.getPitType(), PitType.HOME);
        Assert.assertEquals(0, (int) pit.getNumberOfStones());
        pit.incrementStoneCountBy(10);
        Assert.assertEquals(10, (int) pit.getNumberOfStones());
        pit.decrementStoneCountBy(10);
        Assert.assertEquals(0, (int) pit.getNumberOfStones());
    }

    @Test
    @DisplayName("Verify properties for Player2 playground pits")
    void p2_playgroundPitVerify(){
        Pit pit = boardService.initPit(7);
        Assert.assertEquals(7, (int) pit.getId());
        Assert.assertSame(pit.getOwner().getId(), PlayerID.PLAYER_2);
        Assert.assertSame(pit.getPitType(), PitType.PLAYGROUND);
        Assert.assertEquals(6, (int) pit.getNumberOfStones());
        pit.incrementStoneCountBy(10);
        Assert.assertEquals(16, (int) pit.getNumberOfStones());
        pit.decrementStoneCountBy(10);
        Assert.assertEquals(6, (int) pit.getNumberOfStones());
    }

    @Test
    @DisplayName("Verify properties for Player2 home pit")
    void p2_homePitVerify() {
        Pit pit = boardService.initPit(13);
        Assert.assertEquals(13, (int) pit.getId());
        Assert.assertSame(pit.getOwner().getId(), PlayerID.PLAYER_2);
        Assert.assertSame(pit.getPitType(), PitType.HOME);
        Assert.assertEquals(0, (int) pit.getNumberOfStones());
        pit.incrementStoneCountBy(10);
        Assert.assertEquals(10, (int) pit.getNumberOfStones());
        pit.decrementStoneCountBy(10);
        Assert.assertEquals(0, (int) pit.getNumberOfStones());
    }


    @Test
    @DisplayName("Verify initialized board state")
    void initBoard() {
        int sizeOfBoard = boardService.getBoard().getAllPits().size();
        Assert.assertEquals(14, sizeOfBoard);

        int totalExpectedStones = 6 * 12;
        List<Pit> allPits = boardService.getBoard().getAllPits();
        int sum = allPits
                .stream()
                .mapToInt(Pit::getNumberOfStones)
                .sum();
        Assert.assertEquals(sum, totalExpectedStones);

        Assert.assertEquals(6, (int) allPits.get(0).getNumberOfStones());
        Assert.assertEquals(PlayerID.PLAYER_1, allPits.get(0).getOwner().getId());
        Assert.assertEquals(PitType.PLAYGROUND, allPits.get(0).getPitType());

        Assert.assertEquals(0, (int) allPits.get(6).getNumberOfStones());
        Assert.assertEquals(PlayerID.PLAYER_1, allPits.get(6).getOwner().getId());
        Assert.assertEquals(PitType.HOME, allPits.get(6).getPitType());

        Assert.assertEquals(6, (int) allPits.get(7).getNumberOfStones());
        Assert.assertEquals(PlayerID.PLAYER_2, allPits.get(7).getOwner().getId());
        Assert.assertEquals(PitType.PLAYGROUND, allPits.get(7).getPitType());

        Assert.assertEquals(0, (int) allPits.get(13).getNumberOfStones());
        Assert.assertEquals(PlayerID.PLAYER_2, allPits.get(13).getOwner().getId());
        Assert.assertEquals(PitType.HOME, allPits.get(13).getPitType());
    }

    @Test
    void getPitByID() {
        Pit p = boardService.getPitByID(0);
        Assert.assertEquals(0, (int) p.getId());
        Assert.assertEquals(PlayerID.PLAYER_1, p.getOwner().getId());
        Assert.assertEquals(6, (int)p.getNumberOfStones());
    }

    @Test
    @DisplayName("Verify if exception is thrown while fetching invalid pit ID")
    void getPitOfInvalidID() {
        assertThrows(IndexOutOfBoundsException.class, () -> boardService.getPitByID(100));
    }

    @Test
    @DisplayName("Do pits in specified range have 0 stones?")
    void arePitsInRangeEmpty() {
        int low = 0;
        int high = 6;
        Assert.assertFalse(boardService.arePitsInRangeEmpty(low, high));
        for (int i = low; i < high; i ++)
            boardService.getPitByID(i).setNumberOfStones(0);
        Assert.assertTrue(boardService.arePitsInRangeEmpty(low, high));
    }

    @Test
    @DisplayName("After one player makes one move, verify conditions for next player toggle")
    void doesPlayerContinue() {
        // Player_1 starting from pit 0 will end at home and thus player continues
        Player player = playerService.getP1();
        Pit pit = boardService.getPitByID(0);
        Move move = new Move(player, pit);
        boardService.updateBoardForMove(move);
        Assert.assertTrue(boardService.doesPlayerContinue());

        // For the next move, if player 1 moves from pit 1, player will not continue
        pit = boardService.getPitByID(1);
        move = new Move(player, pit);
        boardService.updateBoardForMove(move);
        Assert.assertFalse(boardService.doesPlayerContinue());
    }

    @Test
    void getPlayerHomeCount() {
        Player player = playerService.getP1();
        // Home-count should be 0 at the beginning
        Assert.assertEquals(0, boardService.getPlayerHomeCount(player));

        Pit pit = boardService.getPitByID(0);
        Move move = new Move(player, pit);
        boardService.updateBoardForMove(move);
        Assert.assertEquals(1, boardService.getPlayerHomeCount(player));

    }

    @Test
    @DisplayName("Verify collecting the remaining stones after end-game")
    void collectRemainingStones() {
        // The game is not over yet, but if we collect stones at the beginning,
        // we should get 36 (6 stones for each of 6 pits) stones on each home pit
        boardService.collectRemainingStones();
        int homeCount1 = boardService.getPlayerHomeCount(playerService.getP1());
        int homeCount2 = boardService.getPlayerHomeCount(playerService.getP2());
        Assert.assertEquals(36, homeCount1);
        Assert.assertEquals(36, homeCount2);
    }

    @Test
    @DisplayName("Verify board status when player 1 moves from pit 0 after board init")
    void doesP1Move0Work(){
        Player player = playerService.getP1();
        int lowerBound = 0;
        int upperBound = 6;
        Pit pit = boardService.getPitByID(lowerBound);
        Move move = new Move(player, pit);
        boardService.updateBoardForMove(move);
        Assert.assertEquals(0, (int) boardService.getPitByID(0).getNumberOfStones());
        for (int i = lowerBound + 1; i < upperBound; i ++)
            Assert.assertEquals(7, (int) boardService.getPitByID(i).getNumberOfStones());
        Assert.assertEquals(1, (int) boardService.getPitByID(upperBound).getNumberOfStones());
        Assert.assertTrue(boardService.doesPlayerContinue());
    }

    @Test
    @DisplayName("Verify simulated board status if player 2 moves from pit 7 after board init")
    void doesP2Move7Work(){
        Player player = playerService.getP2();
        int lowerBound = 7;
        int upperBound = 13;
        Pit pit = boardService.getPitByID(lowerBound);
        Move move = new Move(player, pit);
        boardService.updateBoardForMove(move);
        Assert.assertEquals(0, (int) boardService.getPitByID(lowerBound).getNumberOfStones());
        for (int i = lowerBound + 1; i < upperBound; i ++)
            Assert.assertEquals(7, (int) boardService.getPitByID(i).getNumberOfStones());
        Assert.assertEquals(1, (int) boardService.getPitByID(upperBound).getNumberOfStones());
        Assert.assertTrue(boardService.doesPlayerContinue());
    }

    @Test
    @DisplayName("Verify board status when player 1 moves from pit 1 after board init")
    void doesP1Move1Work(){
        Player player = playerService.getP1();
        Pit pit = boardService.getPitByID(1);
        Move move = new Move(player, pit);
        boardService.updateBoardForMove(move);
        Assert.assertEquals(6, (int) boardService.getPitByID(0).getNumberOfStones());
        Assert.assertEquals(0, (int) boardService.getPitByID(1).getNumberOfStones());
        int lowerBound = 2;
        int upperBound = 6;
        for (int i = lowerBound; i < upperBound; i ++)
            Assert.assertEquals(7, (int) boardService.getPitByID(i).getNumberOfStones());
        Assert.assertEquals(1, (int) boardService.getPitByID(upperBound).getNumberOfStones());
        Assert.assertEquals(7, (int) boardService.getPitByID(7).getNumberOfStones());
        Assert.assertFalse(boardService.doesPlayerContinue());
    }

    @Test
    @DisplayName("Verify simulated board status if player 2 moves from pit 8 after board init")
    void doesP2Move8Work(){
        Player player = playerService.getP2();
        Pit pit = boardService.getPitByID(8);
        Move move = new Move(player, pit);
        boardService.updateBoardForMove(move);
        Assert.assertEquals(6, (int) boardService.getPitByID(7).getNumberOfStones());
        Assert.assertEquals(0, (int) boardService.getPitByID(8).getNumberOfStones());
        int lowerBound = 9;
        int upperBound = 13;
        for (int i = lowerBound; i < upperBound; i ++)
            Assert.assertEquals(7, (int) boardService.getPitByID(i).getNumberOfStones());
        Assert.assertEquals(1, (int) boardService.getPitByID(upperBound).getNumberOfStones());
        // Below value is 7  since board is at the reset condition with current move being the first one
        Assert.assertEquals(7, (int) boardService.getPitByID(0).getNumberOfStones());
        Assert.assertFalse(boardService.doesPlayerContinue());
    }

    @Test
    @DisplayName("Verify capture opposite stones condition")
    void captureOppositeStones() {
        boardService.initBoard();
        boardService.getPitByID(0).setNumberOfStones(1);
        boardService.getPitByID(1).setNumberOfStones(0);
        playerService.initPlayers();
        Player player = playerService.getP1();
        Pit pit = boardService.getPitByID(0);
        Move move = new Move(player, pit);

        int stoneCount = boardService.getPitByID(6).getNumberOfStones();
        Assert.assertEquals(0, stoneCount);

        stoneCount = boardService.getPitByID(13).getNumberOfStones();
        Assert.assertEquals(0, stoneCount);

        stoneCount = boardService.getPitByID(0).getNumberOfStones();
        Assert.assertEquals(1, stoneCount);

        stoneCount = boardService.getPitByID(1).getNumberOfStones();
        Assert.assertEquals(0, stoneCount);

        boardService.updateBoardForMove(move);

        stoneCount = boardService.getPitByID(6).getNumberOfStones();
        Assert.assertEquals(7, stoneCount);

        stoneCount = boardService.getPitByID(13).getNumberOfStones();
        Assert.assertEquals(0, stoneCount);
    }
}