package com.zkwd.server.game;

import java.util.ArrayList;

import javafx.util.Pair;

/**
 * Master class for a singular game of Go.
 */
public class GoGame {

  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  private Player black;
  private Player white;

  /**
   * Game state information
   */
  private Board board;
  private ArrayList<Chain> blackChains;
  private ArrayList<Chain> whiteChains;

  /**
   * Turn information
   */
  private int round = 0;
  private int turn = BLACK; // turn is -1 when black goes, 1 when white goes

  /**
   * GoGame uses sockets to communicate with player applications separately from
   * the PlayerHandler class.
   * @param host black pieces
   * @param joinee white pieces
   */
  public GoGame(Player host, Player joinee, int boardSize) {
    black = host;
    white = joinee;

    board = new Board(boardSize);
  }

  /**
   * Mediate a game of Go between the two players.
   */
  public void run() {
    /**
     * Make both players enter the game state on client-side.
     */
    black.sendMessage("_connect");
    white.sendMessage("_connect");

    Player currentPlayer = black;
    Player otherPlayer = white;

    black.sendMessage("game_black");
    white.sendMessage("game_white");

    black.sendMessage(board.prepareBoardString());
    white.sendMessage(board.prepareBoardString());

    /**
     * !! GAME LOOP !!
     */
    while (true) {

      currentPlayer.sendMessage("game_go");

      try {
        Pair<Integer, Integer> move = currentPlayer.getMove();

        // move validity
        if (board.correctMove(move.getKey(), move.getValue(), turn)) {

          currentPlayer.sendMessage("game_correct");
          board.putStone(move.getKey(), move.getValue(), turn);
          System.out.println(move.getKey() + " " + move.getValue());
          String updatedBoard = board.prepareBoardString();

          currentPlayer.sendMessage(updatedBoard);
          otherPlayer.sendMessage(updatedBoard);

        } else {
          currentPlayer.sendMessage("game_incorrect");
          continue;
        }

      } catch (MoveException e) {
        e.printStackTrace();

        // make player redo turn
        currentPlayer.sendMessage("game_incorrect");
      } catch (GameException e) {
        e.printStackTrace();

        // TODO : Exit game.
      }

      if (currentPlayer == black) {
        currentPlayer = white;
        otherPlayer = black;
      } else {
        currentPlayer = black;
        otherPlayer = white;
      }
      turn = -(turn);
    }
  }
}
