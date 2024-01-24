package com.zkwd.server.game.players;

import java.util.Random;

import com.zkwd.server.game.gamestate.Board;

/**
 * Contains functionality for communication between game (on the server-side) and client application.
 */
public class CPUPlayer implements Player{

    private Board board;

    private String lastMove;

    public CPUPlayer(Board board, int color) {
        this.board = board;
    }

    /**
     * The computer receives a message and does something. Probably usually not much.
     */
    public void sendMessage(String message){
        // the computer is going to get sent its color, and the board size. set those here
    }

    /**
     * Generate a random (but pretty good) move.
     */
    public String getNextMessage() {
        System.out.println("!! \t generating bot move...");

        Random r = new Random();

        // TODO : better
        int x = r.nextInt() % board.getSize();
        int y = r.nextInt() % board.getSize();
        
        lastMove = "move:" + x + "," + y;
        return lastMove;
    }

    /**
     * Get the bot's last move.
     */
    public String getLastMessage() {
        return lastMove;
    }

    /**
     * The bot automatically responds true to game end requests.
     */
    public boolean requestConfirmation() {
        return true;
    }

    // do nothing
    public void clear() {
    }
}
