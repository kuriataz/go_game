package com.zkwd.server.game.players;

import java.io.IOException;

import com.zkwd.server.connection.SocketReceiver;
import com.zkwd.server.game.exceptions.GameException;
import com.zkwd.server.game.exceptions.MoveException;

import javafx.util.Pair;

/**
 * Contains functionality for communication between game (on the server-side) and client application.
 */
public class ClientPlayer implements Player{
    private SocketReceiver socket;

    public ClientPlayer(SocketReceiver socket) throws IOException {
        this.socket = socket;
    }

    /**
     * Sends a message to the player app.
     * @param message The string to be sent.
     */
    public void sendMessage(String message){
        System.out.println("--sending: " + message);
        socket.send(message);
    }

    /**
     * Waits for a message back from the player app, and converts it into a move.
     * @return The received string.
     * @throws IOException
     */
    public Pair<Integer, Integer> getMove() throws MoveException, GameException {
        try {

            String rl = socket.getNextMessage();
            System.out.println("player move is: " + rl);
            String[] parts = rl.split(" ");
            if (parts.length != 2) {
                // error

                // parts length is normally 4 but change that later
            }

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            if (x == -2) {
                // EXIT GAME CODE
                throw new GameException();
            }

            System.out.println("received move: (" + x + ", " + y +")");

            Pair<Integer, Integer> coords = new Pair<Integer,Integer>(x, y);

            return coords;

        } catch (NumberFormatException e) {
            // The transmitted move was incorrect - current player must try again
            throw new MoveException();
        }
    }


    /**
     * Gets the socket.
     * @return socket
     */
    public SocketReceiver getSocket() {
        return socket;
    }

    /**
     * 
     * @return
     */
    public boolean hasExited() {
        return socket.hasExited();
    }
}
