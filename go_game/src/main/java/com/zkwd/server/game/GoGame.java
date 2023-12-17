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
  //private ArrayList<Chain> blackChains;
  //private ArrayList<Chain> whiteChains;

  /**
   * Turn information
   */
  private int round = 0;
  private int turn = BLACK; // turn is -1 when black goes, 1 when white goes

  /**
   * GoGame uses sockets to communicate with player applications separately from the PlayerHandler class.
   * @param host black pieces
   * @param joinee white pieces
   * @exception IOException pass this back to player handler
   */
  public GoGame(Socket host, Socket joinee) throws IOException{
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

    boolean v = true;

    /**
     * !! GAME LOOP !!
     */
    while (!currentPlayer.getSocket().isClosed()) {

      currentPlayer.send("game_go");

      try {
        String input = currentPlayer.await();

        // move validity
        if(v){
          currentPlayer.send("game_correct");

          currentPlayer.send("you said: " + input);
          otherPlayer.send("they said: " + input);

        } else {
          currentPlayer.send("game_incorrect");
          continue;
        }

      } catch (IOException e){
        e.printStackTrace();
        
        // make player redo turn
        currentPlayer.send("game_incorrect");
      }

      
      if(currentPlayer == black){
        currentPlayer = white;
        otherPlayer = black;
      } else {
        currentPlayer = black;
        otherPlayer = white;
      }

      // // player makes move or passes, and sends that here
      // currentPlayer.send("game_go");
      // currentPlayer.send("game_" + round + "_" + board.prepareBoardString());
      // String move = currentPlayer.await();
      // System.out.println("game received " + move);

      // // pass
      // if (move.equals("move:pass")) {
      //   System.out.println("1." + move);
        
      //   // if player passes, change the turn and go to next loop
      //   if (currentPlayer == black) {
      //     currentPlayer = white;
      //   } else {
      //     currentPlayer = black;
      //   }
      // } else {
      //   System.out.println("1." + move);
      //   // move format is "move:x y"
      //   move = move.substring("move:".length());
      //   System.out.println("2." + move);

      //   try {
      //     int x = Integer.parseInt(move.split(" ")[0]);
      //     int y = Integer.parseInt(move.split(" ")[1]);

      //     // check move for correctness
      //     boolean correct = board.correctMove(x, y, turn);

      //     if (correct) {
      //       System.out.println("this is a test");

      //       if (currentPlayer == black) {
      //         board.putBlack(x, y);
      //         currentPlayer = white;
      //         turn = WHITE;
      //       } else {
      //         board.putWhite(x, y);
      //         currentPlayer = black;
      //         turn = BLACK;
      //       }

      //       // send correct signal: being the new board to display
      //       String newBoard = board.prepareBoardString();
      //       white.send("game_" + round + "_" + newBoard);
      //       black.send("game_" + round + "_" + newBoard);

      //       if(turn == WHITE){
      //         round++;
      //       }
      //     } else {
      //       // send incorrect signal - current player must go again
      //       currentPlayer.send("_game_incorrect");
      //       currentPlayer.send("game_goagain");
      //     }
      //   } catch (NumberFormatException e) {
      //     // the transmitted move was somehow incorrect - current player must
      //     // try again
      //     currentPlayer.send("game_goagain");
      //   }
      // }
    }
  }
}
