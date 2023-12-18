package com.zkwd.server.game;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
  private Board board = new Board(3);
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
   * @exception IOException pass this back to player handler
   */
  public GoGame(Socket host, Socket joinee) throws IOException {
    black = new Player(host);
    white = new Player(joinee);
  }

  /**
   * Mediate a game of Go between the two players.
   */
  public void run() {
    /**
     * Make both players enter the game state on client-side.
     */
    System.out.println("sending!!");
    black.send("_connect");
    white.send("_connect");

    Player currentPlayer = black;
    Player otherPlayer = white;

    black.send("game_black");
    white.send("game_white");

    /**
     * !! GAME LOOP !!
     */
    while (!currentPlayer.getSocket().isClosed()) {

      currentPlayer.send("game_go");

      try {
        // clickedPosition has format "x y"
        String clickedPosition = currentPlayer.await();
        int[] coordinates = splitMove(clickedPosition);

        // move validity
        if (board.correctMove(coordinates[0], coordinates[1], turn)) {
          currentPlayer.send("game_correct");
          board.putStone(coordinates[0], coordinates[1], turn);
          System.out.println(coordinates[0] + " " + coordinates[1]);
          String updatedBoard = board.prepareBoardString();

          currentPlayer.send(updatedBoard);
          otherPlayer.send(updatedBoard);

        } else {
          currentPlayer.send("game_incorrect");
          continue;
        }

      } catch (IOException e) {
        e.printStackTrace();

        // make player redo turn
        currentPlayer.send("game_incorrect");
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

  private int[] splitMove(String clickedPosition) {
    int[] coordinates = new int[2];

    try {
      String[] parts = clickedPosition.split(" ");
      if (parts.length != 2) {
        // error
      }

      int x = Integer.parseInt(parts[0]);
      int y = Integer.parseInt(parts[1]);

      coordinates[0] = x;
      coordinates[1] = y;

    } catch (NumberFormatException e) {
      // The transmitted move was incorrect - current player must try again
    }

    return coordinates;
  }

  // private boolean checkValidity(String move) {
  //   boolean valid = false;
  //   try {
  //     int x = Integer.parseInt(move.split(" ")[0]);
  //     int y = Integer.parseInt(move.split(" ")[1]);

  //     // check move for correctness
  //     valid = board.correctMove(x, y, turn);

  //   } catch (NumberFormatException e) {
  //     // the transmitted move was somehow incorrect - current player must
  //     // try again
  //   }
  //   return valid;
  // }
}
