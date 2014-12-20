package com.example.server;

import java.io.Serializable;

public class GameStartOperation  implements Serializable {
    private String firstPlayer;
    private String secondPlayer;

    public GameStartOperation(String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public void setSecondPlayer(String secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public String getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public String getSecondPlayer() {
        return secondPlayer;
    }

    @Override
    public String toString() {
        return firstPlayer + "(x)" + " - " + secondPlayer + "(o)";
    }
}
