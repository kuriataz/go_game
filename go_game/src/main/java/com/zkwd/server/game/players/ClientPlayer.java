package com.zkwd.server.game.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.zkwd.server.game.exceptions.GameException;
import com.zkwd.server.game.exceptions.MoveException;

import javafx.util.Pair;

/**
 * Contains functionality for communication between game (on the server-side) and client application.
 */
public class ClientPlayer implements Player{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientPlayer(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Sends a message to the player app.
     * @param message The string to be sent.
     */
    public void sendMessage(String message){
        System.out.println("--sending: " + message);
        out.println(message);
    }

    /**
     * Waits for a message back from the player app.
     * @return The received string.
     * @throws IOException
     */
    public Pair<Integer, Integer> getMove() throws MoveException, GameException {
        try {

            String rl = in.readLine();
            String[] parts = rl.split(" ");
            if (parts.length != 2) {
                // error
            }

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            Pair<Integer, Integer> coords = new Pair<Integer,Integer>(x, y);

            return coords;

        } catch (NumberFormatException e) {
            // The transmitted move was incorrect - current player must try again
            throw new MoveException();
        } catch (IOException e) {
            // an IOException occurred
            throw new GameException();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
