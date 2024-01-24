package com.zkwd.server.game.players;

import java.io.IOException;

import com.zkwd.server.connection.SocketReceiver;

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

    public String getNextMessage() {
        return socket.getNextMessage();
    }

    public String getLastMessage() {
        return socket.getLastMessage();
    }

    /**
     * Get the answer to a confirmation request.
     * @return true, if the next message through the socket is "yes"
     */
    public boolean requestConfirmation() {
        String rl = socket.getNextMessage();

        return rl.equals("yes");
    }

    /**
     * 
     * @return
     */
    public boolean hasExited() {
        return socket.hasExited();
    }

    public void clear() {
        socket.clear();
    }
}
