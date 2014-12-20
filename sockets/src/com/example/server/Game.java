package com.example.server;

public class Game {
    public static final int EMPTY = 0;
    public static final int TIC = 1;
    public static final int TAC = 2;

    private static final int SIZE = 3;

    private final String p1;
    private final String p2;
    private final byte[][] field;
    private GameTurnOperation next;
    private String winner = null;

    public Game(String firstPlayer, String secondPlayer) {
        p1 = firstPlayer;
        p2 = secondPlayer;

        // Create new field and fill it with empty cells
        field = new byte[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                field[i][j] = EMPTY;
            }
        }
    }

    public int getSize() {
        return SIZE;
    }

    public byte[][] getField() {
        return field;
    }

    public String getWinner() {
        return winner;
    }

    public boolean isFinished() {
        update();
        // Game is not finished until the winner is unknown
        return winner != null;
    }

    public boolean nextTurn(GameTurnOperation op) {
        int x = op.getX(), y = op.getY();
        // Check if coordinates are valid
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || field[x][y] != EMPTY) {
            return false;
        }
        next = op;
        return true;
    }

    private void update() {
        if (next == null) {
            // If no turn to process then just exit
            return;
        }

        // Update our field with new coordinates
        if (p1.equals(next.getName())) {
            field[next.getX()][next.getY()] = TIC;
        } else {
            field[next.getX()][next.getY()] = TAC;
        }

        // count x, o in each row
        int count1, count2;
        for (int i = 0; i < SIZE; i++) {
            count1 = 0;
            count2 = 0;
            for (int j = 0; j < SIZE; j++) {
                if (field[i][j] == TIC) {
                    count1++;
                } else if (field[i][j] == TAC) {
                    count2++;
                }
            }

            checkWinner(count1, count2);
        }

        // count x, o in each column
        for (int i = 0; i < SIZE; i++) {
            count1 = 0;
            count2 = 0;
            for (int j = 0; j < SIZE; j++) {
                if (field[j][i] == TIC) {
                    count1++;
                } else if (field[i][j] == TAC) {
                    count2++;
                }
            }

            checkWinner(count1, count2);
        }

        // Finally, count x, o on each diagonal row
        count1 = 0;
        count2 = 0;
        for (int i = 0; i < SIZE; i++) {
            if (field[i][i] == TIC) {
                count1++;
            } else if (field[i][i] == TAC) {
                count2++;
            }
        }
        checkWinner(count1, count2);
        count1 = 0;
        count2 = 0;
        for (int i = 0; i < SIZE; i++) {
            if (field[i][SIZE - i - 1] == TIC) {
                count1++;
            } else if (field[i][i] == TAC) {
                count2++;
            }
        }
        checkWinner(count1, count2);

        if (winner == null) {
            // Count empty cells left. No empty cells - game finished
            int emptyCount = 0;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (field[i][j] == EMPTY) {
                        emptyCount++;
                    }
                }
            }
            if (emptyCount == 0) {
                winner = "nobody (no turns left)";
            }
        }
    }

    private void checkWinner(int count1, int count2) {
        if (count1 == SIZE) {
            winner = p1;
        } else if (count2 == SIZE) {
            winner = p2;
        }
    }

}
