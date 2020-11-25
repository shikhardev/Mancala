import React, { Component } from "react";
import "./button.css";

class BottomButtons extends Component {
  render() {
    const { gameStarted, startFunction } = this.props;
    let buttonText = null;
    if (gameStarted) buttonText = "Reset Game";
    else buttonText = "Start Game";
    return (
      <button
        className="btn btn-primary btn-lg buttons"
        onClick={startFunction}
      >
        {buttonText}
      </button>
    );
  }
}

export default BottomButtons;
