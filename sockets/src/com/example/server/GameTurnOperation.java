package com.example.server;

import java.io.Serializable;

public class GameTurnOperation implements Serializable {
    private int y;
    private int x;
    private String name;

    public GameTurnOperation(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
