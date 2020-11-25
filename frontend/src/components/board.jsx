import React, { Component } from "react";
import "react-toastify/dist/ReactToastify.css";
import Pit from "./pit";
import "./board.css";

class Board extends Component {
  render() {
    const { allPits, nextPlayer, isEndGame, onMove, gameStarted } = this.props;
    let b = <div></div>;
    if (isEndGame === true) {
      b = <div className="board-wrapper"></div>;
    } else if (gameStarted === false) {
      b = <div className="board-wrapper"></div>;
    } else {
      const firstPlayground = allPits.splice(0, 7);
      const firstHome = firstPlayground.pop();
      const secondPlayground = allPits.splice(0, 7);
      const secondHome = secondPlayground.pop();

      b = (
        <div className="board-wrapper">
          <Pit onMove={onMove} pit={firstHome} currentPlayer={nextPlayer} />
          <div className="board">
            <div className="board-up">
              {firstPlayground.map((pit, key) => (
                <Pit
                  onMove={onMove}
                  key={key}
                  pit={pit}
                  currentPlayer={nextPlayer}
                />
              ))}
            </div>
            <div className="board-down">
              {secondPlayground.map((pit, key) => (
                <Pit
                  onMove={onMove}
                  key={key}
                  pit={pit}
                  currentPlayer={nextPlayer}
                />
              ))}
            </div>
          </div>
          <Pit onMove={onMove} pit={secondHome} currentPlayer={nextPlayer} />
        </div>
      );
    }

    return <div className="board-wrapper">{b}</div>;
  }
}

export default Board;
