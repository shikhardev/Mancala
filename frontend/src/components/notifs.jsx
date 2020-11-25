import React, { Component } from "react";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

toast.configure();
class Notifs extends Component {
  render() {
    const { error, endGame, winner, legalMove } = this.props.gameStatus;
    let message = null;
    if (endGame === true) {
      message = winner && winner.id ? `Winner : ${winner.id}` : `It's a draw!`;
    } else if (error) {
      message = "Please recheck network connection.";
    } else if (!legalMove) {
      message = "Illegal move!";
    }
    toast.error(message, {
      position: toast.POSITION.BOTTOM_CENTER,
      autoClose: 1000,
    });

    return <div></div>;
  }
}

export default Notifs;
