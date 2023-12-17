package com.zkwd.server.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Contains functionality for communication between game (on the server-side) and client application.
 */
public class Player {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    Player(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Sends a message to the player app.
     * @param message The string to be sent.
     */
    public void send(String message){
        System.out.println("--sending: " + message);
        out.println(message);
    }

    /**
     * Waits for a message back from the player app.
     * @return The received string.
     * @throws IOException
     */
    public String await() throws IOException {
        return in.readLine();
    }

    public Socket getSocket() {
        return socket;
    }
}
