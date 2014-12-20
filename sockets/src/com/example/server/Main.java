package com.example.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // If not enough arguments - print help
        if (args.length < 2) {
            System.out.println("Usage:\njava com.example.server.Main <name> <port> [<host>]");
            return;
        }

        // Start either server or client (client has server's host as third param)
        String name = args[0];
        int port = Integer.parseInt(args[1]);
        if (args.length > 2) {
            System.out.println("Client mode");
            String host = args[2];
            GameClient.startGame(name, port, host);
        } else {
            System.out.println("Server mode");
            GameClient.waitForGame(name, port);
        }
    }
}
