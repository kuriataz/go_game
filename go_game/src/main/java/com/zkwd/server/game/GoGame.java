package com.zkwd.server.game;

import com.zkwd.server.game.exceptions.GameException;
import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;
import com.zkwd.server.game.players.CPUPlayer;
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

  private boolean isBotGame = false;

  private Player black;
  private Player white;

  // gamestate information
  private Board board;
  // turn information
  // private int round = 0;
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
  public String startGame() {

    // tell both players to enter the ingame app state
    broadcast("_connect");

    Player currentPlayer = black;
    Player otherPlayer = white;

    // tell players their respective colors
    black.sendMessage("game_black");
    white.sendMessage("game_white");

    broadcast(board.prepareBoardString());

    boolean requested = false;
    boolean valid = false;

    /**
     * !! GAME LOOP !!
     */
    while (currentPlayer instanceof ClientPlayer || otherPlayer instanceof
                                                        ClientPlayer) {
      valid = true;
      // tell players whose turn it is
      currentPlayer.sendMessage("game_go");
      otherPlayer.sendMessage("game_opp");

      // if other player requested game end, ask current player.
      if (requested) {
        currentPlayer.sendMessage("game_reqend");
        // await response
        if (currentPlayer.requestConfirmation()) {
          // go to results
          broadcast("game_end");
          break;
        } else {
          // skip to next round, switch players
          broadcast("game_noend");
          if (turn == -1) {
            currentPlayer = white;
            otherPlayer = black;
          } else {
            currentPlayer = black;
            otherPlayer = white;
          }
          turn = -turn;
          requested = false;
          continue;
        }
      } else {
        // signal to players that there were no requests
        currentPlayer.sendMessage("game_noreq");
        broadcast("game_noend");
      }

      // listen for exit singals from both players, and move from current
      String csig, osig;

      currentPlayer.clear();
      otherPlayer.clear();

      do {
        System.out.println(turn + ": awaiting input signals...");

        do {
          // important: get current messages without waiting.
          csig = currentPlayer.getLastMessage();
          osig = otherPlayer.getLastMessage();
          // !!! LOADBEARING SOUT DO NOT DELETE
          System.out.print("");
        } while (!csig.equals("exit") && !osig.equals("exit") &&
                 !csig.startsWith("move:"));

        System.out.println("received: " + csig + "|" + osig + ". parsing...");
        // parse signal - was it an abandonment, or a move?
        if (csig.equals("exit")) {
          currentPlayer.sendMessage("game_err");
          // current player abandoned
          if (isBotGame) {
            return board.getHistory();
          }
          otherPlayer.sendMessage("game_abdn");
          if (otherPlayer.requestConfirmation()) {
            // replace other player with CPU
            System.out.println("\t\t REPLACING currentPlayer");
            currentPlayer = new CPUPlayer(board, turn);
            if (turn == -1) {
              black = currentPlayer;
            } else {
              white = currentPlayer;
            }
            isBotGame = true;
            //
          } else {
            // exit game
            otherPlayer.sendMessage("game_err");
            return board.getHistory();
            //
          }
          //
        } else if (osig.equals("exit")) {
          // other player abandoned
          otherPlayer.sendMessage("game_err");
          if (isBotGame) {
            return board.getHistory();
          }
          currentPlayer.sendMessage("game_abdn");
          if (currentPlayer.requestConfirmation()) {
            // replace other player with CPU
            System.out.println("\t\t REPLACING otherPlayer");
            otherPlayer = new CPUPlayer(board, -turn);
            if (turn == 1) {
              black = otherPlayer;
            } else {
              white = otherPlayer;
            }
            isBotGame = true;
            //
          } else {
            // exit game
            currentPlayer.sendMessage("game_err");
            return board.getHistory();
            //
          }
          //
        }
      } while (!csig.startsWith("move:"));

      System.out.println(turn + ": detected a move! checking validity...");

      try {
        Pair<Integer, Integer> move = parseMove(csig);

        if (move.getKey() == -1) {
          // requested
          requested = true;
          System.out.println(turn + ": player requested end...");
          broadcast("game_vrfd");
          //
        } else if (board.correctMove(move.getKey(), move.getValue(), turn)) {
          board.putStone(move.getKey(), move.getValue(), turn);
          board.removeCapturedStones();
          board.removeCapturedChains();

          System.out.println("valid move");
          System.out.println("history: " + board.getHistory());
          broadcast("game_vrfd");
        } else {
          // tell current player their move is invalid.
          System.out.println("invalid move");
          broadcast("game_vrfd");
          // continue without changing player
          valid = false;
        }

      } catch (MoveException e) {
        // move was incorrect - try again
        e.printStackTrace();
        // continue without changing player
        valid = false;

      } catch (GameException e) {
        e.printStackTrace();
        // something went wrong and the game must close
        broadcast("game_err");
        return board.getHistory();
      }

      System.out.println(turn + ": checking if players abandoned...");
      // check if either player has abandoned the match while we weren't
      // listening
      if (checkAbandoned(otherPlayer)) {
        otherPlayer.sendMessage("game_err");
        if (isBotGame) {
          return board.getHistory();
        }
        currentPlayer.sendMessage("game_abdn");
        if (currentPlayer.requestConfirmation()) {
          // replace other player with CPU
          System.out.println("\t\t REPLACING otherPlayer2");
          otherPlayer = new CPUPlayer(board, -turn);
          if (turn == 1) {
            black = otherPlayer;
          } else {
            white = otherPlayer;
          }
          isBotGame = true;
          //
        } else {
          // exit game
          currentPlayer.sendMessage("game_err");
          return board.getHistory();
          //
        }
      } else {
        currentPlayer.sendMessage("game_noabdn");
      }

      if (checkAbandoned(currentPlayer)) {
        currentPlayer.sendMessage("game_err");
        if (isBotGame) {
          return board.getHistory();
        }
        otherPlayer.sendMessage("game_abdn");
        if (otherPlayer.requestConfirmation()) {
          System.out.println("\t\t REPLACING currentPlayer2");
          // replace other player with CPU
          currentPlayer = new CPUPlayer(board, turn);
          if (turn == -1) {
            black = currentPlayer;
          } else {
            white = currentPlayer;
          }
          isBotGame = true;
          //
        } else {
          // exit game
          otherPlayer.sendMessage("game_err");
          return board.getHistory();
          //
        }
      } else {
        otherPlayer.sendMessage("game_noabdn");
      }

      System.out.println(turn + ": sending out board information...");
      // send out new board information
      broadcast(board.prepareBoardString());

      // switch turns, if move was valid
      if (valid) {
        if (turn == -1) {
          currentPlayer = white;
          otherPlayer = black;
        } else {
          currentPlayer = black;
          otherPlayer = white;
        }
        turn = -turn;
      }
    }

    System.out.println("game ended!");
    calculateScore();
    return board.getHistory();
  }

  private void broadcast(String message) {
    black.sendMessage(message);
    white.sendMessage(message);
  }

  /**
   * Calculate game score. This method is called once both players agree to end
   * the game.
   */
  private void calculateScore() {
    // TODO : write
  }

  /**
   * Parse a move.
   * @param move formatted: "move:x,y"
   * @return (x, y)
   */
  private Pair<Integer, Integer> parseMove(String move)
      throws MoveException, GameException {
    try {
      String[] parts = move.substring(5).split(",");

      int x = Integer.parseInt(parts[0]);
      int y = Integer.parseInt(parts[1]);

      Pair<Integer, Integer> coords = new Pair<Integer, Integer>(x, y);

      return coords;
    } catch (NumberFormatException e) {
      // The transmitted move was incorrect - generate another
      throw new MoveException();
    }
  }

  /**
   * Check if a player has abandoned the match.
   * @param p player
   * @return true if they've left
   */
  private boolean checkAbandoned(Player p) {
    if (p instanceof ClientPlayer) {
      // p is a human player - check if the exited flag is set
      return ((ClientPlayer)p).hasExited();
    } else {
      // p is a bot player and therefore is there
      return false;
    }
  }
}