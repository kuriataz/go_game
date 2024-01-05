package com.zkwd.server.game.players;

import java.io.IOException;

import com.zkwd.server.game.exceptions.GameException;
import com.zkwd.server.game.exceptions.MoveException;

import javafx.util.Pair;

/**
 * Contains functionality for communication between game (on the server-side) and client application.
 */
public class CPUPlayer implements Player{

    // private Board board;

    public CPUPlayer() throws IOException {
    }

    /**
     * The computer receives a message and does something. Probably usually not much.
     */
    public void sendMessage(String message){
        // the computer is going to get sent its color, and the board size. set those here
        // also update board state
    }

    /**
     * Generate a random (but pretty good) move.
     */
    public Pair<Integer, Integer> getMove() throws MoveException, GameException {
        
        return new Pair<Integer,Integer>(0, 0);
    }
}
