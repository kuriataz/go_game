package com.zkwd.server.game;

import com.zkwd.server.game.exceptions.GameException;
import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;
import com.zkwd.server.game.gamestate.Chain;
import com.zkwd.server.game.players.Player;
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
    broadcast("_connect");

    Player currentPlayer = black;
    Player otherPlayer = white;

    black.sendMessage("game_black");
    white.sendMessage("game_white");

    broadcast(board.prepareBoardString());

    /**
     * !! GAME LOOP !!
     */
    while (true) {

      currentPlayer.sendMessage("game_go");

      try {
        Pair<Integer, Integer> move = currentPlayer.getMove();

        // !! CURRENT PLAYER REQUESTED END
        if (move.getKey() == -1 && move.getValue() == -1) {
          otherPlayer.sendMessage(board.prepareBoardString());
          otherPlayer.sendMessage("game_req");

          Pair<Integer, Integer> resp = otherPlayer.getMove();
          if (resp.getKey() == -1 && resp.getValue() == -1) {
            // exit game
            broadcast("game_exit");
            break;
            //
          } else {
            // proceed as normal. do not change player
            continue;
            //
          }
        }

        // move validity
        if (board.correctMove(move.getKey(), move.getValue(), turn)) {

          currentPlayer.sendMessage("game_correct");
          board.putStone(move.getKey(), move.getValue(), turn);
          System.out.println(move.getKey() + " " + move.getValue());
          // if (board.correctMove(coordinates[0], coordinates[1], turn)) {
          //   currentPlayer.send("game_correct");
          //   board.putStone(coordinates[0], coordinates[1], turn);
          //   board.removeCapturedStones();
          //   board.removeCapturedChains();
          String updatedBoard = board.prepareBoardString();

          broadcast(updatedBoard);

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

        // tell players to exit to lobby
        broadcast("game_err");

        // self destruct ig
        return;
      }

      if (currentPlayer == black) {
        currentPlayer = white;
        otherPlayer = black;
        ++round;
      } else {
        currentPlayer = black;
        otherPlayer = white;
      }
      turn = -(turn);
    }

    // TODO : figure out who won
  }

  void broadcast(String message) {
    black.sendMessage(message);
    white.sendMessage(message);
  }
}
