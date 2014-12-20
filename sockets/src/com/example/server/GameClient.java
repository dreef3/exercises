package com.example.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    private static final String[] SYMBOLS = {"*", "o", "x"};
    private static GameClient clientInstance;
    private final String name;
    private boolean turn;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Game game;

    /**
     *
     * @param name our player's name
     * @param op info about the game
     * @param in input stream to receive messages from another player
     * @param out output stream of socket to send messages to another player
     * @param isServer server makes the first turn
     */
    public GameClient(String name, GameStartOperation op, ObjectInputStream in, ObjectOutputStream out, boolean isServer) {
        this.name = name;
        this.in = in;
        this.out = out;
        turn = isServer;
        game = new Game(op.getFirstPlayer(), op.getSecondPlayer());
        System.out.println("Started new game:\n" + op);
    }

    public void run() {
        GameTurnOperation op;
        try {
            // Repeat until game is finished
            while (!game.isFinished()) {
                // Print current game field
                printField();
                if (turn) {
                    do {
                        // If it's our turn read coordinates and send the turn to another player
                        op = makeTurn();
                    } while (!game.nextTurn(op));
                    out.writeObject(op);
                } else {
                    // If it's other player's turn then wait for it
                    System.out.println("Waiting for other player's turn...");
                    op = (GameTurnOperation) in.readObject();
                    // Update game field
                    game.nextTurn(op);
                }
                // Switch turn
                turn = !turn;
            }

            // Game finished, print the winner
            printResults(game.getWinner());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printResults(String winner) {
        printField();
        System.out.println("Game finished.\nWinner: " + winner);
    }

    private GameTurnOperation makeTurn() {
        System.out.println("Your turn (x, y): ");
        int x, y;
        // Read coordinates from console
        Scanner scanner = new Scanner(System.in);
        x = scanner.nextInt();
        y = scanner.nextInt();
        return new GameTurnOperation(name, x, y);
    }

    private void printField() {
        byte[][] field = game.getField();
        int size = game.getSize();

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                System.out.print("   ");
                for (int j = 0; j < size; j++) {
                    System.out.print(j + " ");
                }
                System.out.println("\n   -----");
            }

            System.out.print(i + "| ");
            for (int j = 0; j < size; j++) {
                System.out.print(SYMBOLS[field[i][j]] + " ");
            }
            System.out.println();
        }
    }

    public static void startGame(String name, int port, String host) throws IOException, ClassNotFoundException {
        // Connect to the server
        Socket client = new Socket(InetAddress.getByName(host), port);
        System.out.println("Connected to " + host + ":" + port);
        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
        // Send our name to it
        out.writeObject(new GameStartOperation(name));
        // Wait for the server to reply with it's name and start the game
        Object o = in.readObject();
        if (o instanceof GameStartOperation) {
            GameStartOperation op = (GameStartOperation) o;
            clientInstance = new GameClient(name, op, in, out, false);
            clientInstance.run();
        }
    }

    /**
     * Wait for another player to connect and start game
     *
     * @param name our player's name
     * @param port port to listen for connection
     * @throws IOException Port is busy
     * @throws ClassNotFoundException
     */
    public static void waitForGame(String name, int port) throws IOException, ClassNotFoundException {
        // Open socket and listen on it until client is connected
        ServerSocket server = new ServerSocket(port);
        System.out.println("Listening on port " + port);
        Socket client = server.accept();
        System.out.println("Connected new client");
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
        // After client is connected wait for it's name
        Object o = in.readObject();
        if (o instanceof GameStartOperation) {
            GameStartOperation op = (GameStartOperation) o;
            op.setSecondPlayer(name);
            // Send our own name back and start the game
            out.writeObject(op);
            clientInstance = new GameClient(name, op, in, out, true);
            clientInstance.run();
        }
    }
}
