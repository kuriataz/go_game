package com.zkwd.server.game.players;

import com.zkwd.server.game.gamestate.Board;
import java.util.Random;
import javafx.util.Pair;

/**
 * Contains functionality for communication between game (on the server-side)
 * and client application.
 */
public class CPUPlayer implements Player {

  private Board board;

  private int color;

  private String lastMove;

  public CPUPlayer(Board board, int color) {
    this.board = board;
    this.color = color;
  }

  /**
   * The computer receives a message and does something. Probably usually not
   * much.
   */
  public void sendMessage(String message) {
    // the computer is going to get sent its color, and the board size. set
    // those here
  }

  /**
   * Generate a random (but pretty good) move.
   */
  public void generateMove() {
    System.out.println("!! \t generating bot move...");

    // // TODO : better
    Random r = new Random();
    int x = r.nextInt(0, board.getSize());
    int y = r.nextInt(0, board.getSize());

    // Pair<Integer, Integer> cords = bestMove();

    // int x = cords.getKey();
    // int y = cords.getValue();

    lastMove = "move:" + x + "," + y;
  }

  public Pair<Integer, Integer> bestMove() {

    Pair<Integer, Integer> cords = new Pair<Integer, Integer>(0, 0);
    double priority = 0;

    priorityFunction(color);

    for (int i = 0; i != board.getSize(); ++i) {
      for (int j = 0; j != board.getSize(); ++j) {
        if (board.board[i][j].priority > priority) {
          priority = board.board[i][j].priority;
          cords = new Pair<Integer, Integer>(i, j);
        }
      }
    }
    return cords;
  }

  public void priorityFunction(int playerColor) {
    double disOpponent = 0;
    double disPlayer = 0;
    double priority = 0;
    for (int i = 0; i != board.getSize(); ++i) {
      for (int j = 0; j != board.getSize(); ++j) {
        disOpponent = 1 / squareDistance(i, j, -playerColor);
        disPlayer = squareDistance(i, j, playerColor) * 0.5;
        priority = disOpponent + disPlayer;

        if ((i % 8) == 0 || (j % 8) == 0) {
          priority -= 5;
        }
        board.board[i][j].setPriority(priority);
      }
    }
  }

  public double squareDistance(int x, int y, int playerColor) {
    int disSum = 0;
    for (int i = 0; i != board.getSize(); ++i) {
      for (int j = 0; j != board.getSize(); ++j) {
        if (board.board[i][j].getState() == -playerColor) {
          disSum += Math.pow((i - x), 2) + Math.pow((j - y), 2);
        }
      }
    }
    return disSum;
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
  public String getLastMessage() { return lastMove; }

  /**
   * The bot automatically responds true to game end requests.
   */
  public boolean requestConfirmation() { return true; }

  // do nothing
  public void clear() {}
}
