import React, { Component } from "react";
import "./pit.css";

class Pit extends Component {
  render() {
    const { pit, currentPlayer, onMove } = this.props;
    const makeMove = () => onMove(pit.id);
    return (
      <div className={pit.pitType == "PLAYGROUND" ? "pit" : "pit home"}>
        <button className="stone" onClick={makeMove}>
          {pit.numberOfStones}
        </button>
      </div>
    );
  }

  isPitActive(stoneCount, pitOwner, currentPlayer) {
    return stoneCount !== 0 && pitOwner === currentPlayer;
  }
}

export default Pit;
