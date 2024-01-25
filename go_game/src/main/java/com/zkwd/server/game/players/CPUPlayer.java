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
     * The computer receives a message and does something.
     * Update board if message is a board string.
     */
    public void sendMessage(String message){
        System.out.println("bot received: " + message);
        if(message.endsWith("|")) {
            System.out.println("message is a board. updating...");
            // if board is null, create new board
            if (board == null) {
                board = new Board(message.indexOf("|"));
            }
            // set board
            board.setBoard(message);
        }
        // else ignore
        System.out.println("updated :)");
    }

    /**
     * Generate a random (but pretty good) move.
     */
    public void generateMove() {
        System.out.println("!! \t generating bot move...");

        Random r = new Random();

        // TODO : better
        int x = r.nextInt(0, board.getSize());
        int y = r.nextInt(0, board.getSize());
        
        lastMove = "move:" + x + "," + y;
    }

    /**
     * Generate a move and return it
     */
    public String getNextMessage() {
        generateMove();
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
