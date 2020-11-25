import React, { Component } from "react";
import logo from "./logo.svg";
import "./App.css";
import Board from "./components/board";
import Notifs from "./components/notifs";
import BottomButtons from "./components/bottomButtons";
import { startGame, playMove } from "./components/api";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      board: "",
      legalMove: true,
      currentPlayer: "",
      endGame: false,
      winner: "",
      error: false,
    };
  }

  render() {
    const {
      allPits,
      legalMove,
      currentPlayer,
      endGame,
      winner,
      error,
    } = this.state;
    const gameStarted = !!currentPlayer || !!allPits;
    let b = null; // Top row message
    if (!gameStarted) {
      b = <h1>Welcome to Mancala Game</h1>;
    } else if (!endGame) {
      b = <h3>Current Player: {currentPlayer}</h3>;
    } else {
      b = <h3>Winner: {winner.id}</h3>;
    }

    return (
      <div className="game-container">
        <Notifs gameStatus={{ error, legalMove, endGame, winner }}></Notifs>
        {b}
        <Board
          allPits={allPits}
          nextPlayer={currentPlayer}
          isEndGame={endGame}
          isLegalMove={legalMove}
          onMove={this.onMoveApp}
          gameStarted={gameStarted}
        ></Board>
        <BottomButtons
          gameStarted={gameStarted}
          startFunction={this.startGameApp}
        ></BottomButtons>
      </div>
    );
  }

  startGameApp = async () => {
    const data = await startGame().catch(() => this.setState({ error: true }));
    if (data) {
      this.mapData(data);
    }
  };

  onMoveApp = async (pit) => {
    if (!this.state.endGame) {
      const data = await playMove(this.state.currentPlayer, pit).catch(() =>
        this.setState({ error: true })
      );
      if (data) {
        this.mapData(data);
      }
    }
  };

  mapData = (data) => {
    this.setState({
      allPits: data.currentBoardStatus.allPits,
      legalMove: data.legalMove,
      currentPlayer: data.nextPlayer.id,
      endGame: data.endGame,
      winner: data.winner,
      error: false,
    });
  };
}

export default App;
