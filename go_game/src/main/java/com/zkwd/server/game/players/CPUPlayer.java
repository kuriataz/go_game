package com.zkwd.server.game.players;

import java.util.Random;

import com.zkwd.server.game.gamestate.Board;

/**
 * A bot player that generates moves
 */
public class CPUPlayer implements Player{
    
    // the bot's own board
    private Board board;
    // last move
    private String lastMove; // TODO : figure out how to store move priority list.
    // player color
    private int color;
    // indicates whether it is the bot's turn or not
    private boolean turn;

    public CPUPlayer(Board board, int color) {
        this.board = board;
        this.color = color;
            
        generateMoveList();
    }

    public CPUPlayer(int color) {
        this.color = color;
    }

    /**
     * The computer receives a message and does something.
     * Update board if message is a board string.
     */
    public void sendMessage(String message){
        System.out.println("bot received: " + message);
        // have we received board string?
        if(message.endsWith("|")) {
            // if board is null, create new board
            if (board == null) {
                board = new Board(message.indexOf("|"));
            }
            // set board
            board.setBoard(message);
        } else if(message.equals("game_noend")) {
            // this is the last signal thats sent before the game asks a player for a move.
            // generate a move here 
            if (turn) {
                generateMoveList();
            }
        } else if(message.equals("game_go")) {
            // bot round
            turn = true;
        } else if(message.equals("game_no")) {
            // non-bot round
            turn = false;
        }
    }

    /**
     * Generate a random (but pretty good) move.
     */
    public void generateMoveList() {
        System.out.println("!! \t generating bot move...");

        Random r = new Random();

        int x = r.nextInt(0, board.getSize());
        int y = r.nextInt(0, board.getSize());
        
        lastMove = "move:" + x + "," + y;
    }

    /**
     * (unused) Generate a new move and return it.
     */
    public String getNextMessage() {
        generateMoveList();
        return lastMove;
    }

    /**
     * Get the bot's last move.
     */
    public String getLastMessage() {
        if(lastMove == null){
            // for the first time this is called, a move may not have been generated yet
            generateMoveList();
        }
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
