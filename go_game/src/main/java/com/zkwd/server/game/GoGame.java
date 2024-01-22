package com.zkwd.server.game;

import com.zkwd.server.game.exceptions.GameException;
import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;
import com.zkwd.server.game.players.ClientPlayer;
import com.zkwd.server.game.players.Player;

import javafx.util.Pair;

/**
 * Master class for a singular game of Go.
 */
public class GoGame {
  // colors
  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  private Player black;
  private Player white;

  // gamestate information
  private Board board;
  // turn information
  //private int round = 0;
  private int turn = BLACK;

  /**
   * GoGame uses sockets to communicate with player applications separately from
   * the PlayerHandler class.
   * @param host The player object responsible for the black pieces
   * @param joinee The player object responsible for the white pieces
   */
  public GoGame(Player host, Player joinee, int boardSize) {
    black = host;
    white = joinee;

    board = new Board(boardSize);
  }

  /**
   * Mediate a game of Go between the two players.
   */
  public void startGame() {

    // tell both players to enter the ingame app state
    broadcast("_connect");

    Player currentPlayer = black;
    Player otherPlayer = white;

    // tell players their respective colors
    black.sendMessage("game_black");
    white.sendMessage("game_white");

    broadcast(board.prepareBoardString());

    /**
     * !! GAME LOOP !!
     */
    while (true) {
      try {
        // check if either player abandoned match
        // if they did, exit
        if (currentPlayer instanceof ClientPlayer) {
          if (((ClientPlayer)currentPlayer).hasExited()){
            throw new GameException();
          }
          System.out.println("hi curr");
        }
        if (otherPlayer instanceof ClientPlayer) {
          if (((ClientPlayer)otherPlayer).hasExited()){
            throw new GameException();
          }
          System.out.println("hi other");
        }

        // tell current player it is their round
        currentPlayer.sendMessage("game_go");
        Pair<Integer, Integer> move = currentPlayer.getMove();

        if (move.getKey() == -1) {
          // !! CURRENT PLAYER REQUESTED END

          otherPlayer.sendMessage(board.prepareBoardString());
          otherPlayer.sendMessage("game_req");

          Pair<Integer, Integer> resp = otherPlayer.getMove();
          if (resp.getKey() == -1) {
            // exit game
            broadcast("game_exit");
            calculateScore();
            break;
            //
          } else {
            // proceed as normal. do not change player
            continue;
            //
          }
        } else if (board.correctMove(move.getKey(), move.getValue(), turn)) {
          // !! VALID MOVE

          currentPlayer.sendMessage("game_correct");
          board.putStone(move.getKey(), move.getValue(), turn);
          System.out.println(move.getKey() + " " + move.getValue());
          board.removeCapturedStones();
          board.removeCapturedChains();
          String updatedBoard = board.prepareBoardString();

          broadcast(updatedBoard);

        } else {
          // !! INVALID MOVE

          currentPlayer.sendMessage("game_incorrect");
          continue;
        }

      } catch (MoveException e) {
        e.printStackTrace();

        // make player redo turn
        currentPlayer.sendMessage("game_incorrect");
        continue;
      } catch (GameException e) {
        e.printStackTrace();

        // tell players to exit to lobby
        broadcast("game_err");

        // self destruct ig
        return;
      }

      // switch players
      if (currentPlayer == black) {
        currentPlayer = white;
        otherPlayer = black;
        //++round;
      } else {
        currentPlayer = black;
        otherPlayer = white;
      }
      turn = -(turn);
    }
  }

  private void broadcast(String message) {
    black.sendMessage(message);
    white.sendMessage(message);
  }

  /**
   * Calculate game score. This method is called once both players agree to end the game.
   */
  private void calculateScore() {
    // TODO : write
  }
}