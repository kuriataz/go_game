package com.zkwd.server.game.players;

import com.zkwd.server.game.gamestate.Board;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javafx.util.Pair;

/**
 * A bot player that generates moves
 */
public class CPUPlayer implements Player {

  // the bot's own board
  private Board board;
  private String preferredMove;
  // player color
  private int color;
  // indicates whether it is the bot's turn or not
  private boolean turn;
  private int turnCounter;

  // array that contains a list of the best moves together with their priority
  // values
  private ArrayList<Pair<String, Double>> bestMoves = new ArrayList<>();

  public CPUPlayer(Board board, int color) {
    this.board = board;
    this.color = color;

    generateMoveList();
  }

  public CPUPlayer(int color) { this.color = color; }

  /**
   * The computer receives a message and does something.
   * Update board if message is a board string.
   */
  public void sendMessage(String message) {
    System.out.println("bot received: " + message);
    // have we received board string?
    if (message.endsWith("|")) {
      // if board is null, create new board
      if (board == null) {
        board = new Board(message.indexOf("|"));
      }
      // set board
      board.setBoard(message);
    } else if (message.equals("game_noend")) {
      // last signal before move request. only reset on our first turn
      System.out.println("turncounter: " + turnCounter);
      if (turn && turnCounter > 0) {
        generateMoveList();
      }
    } else if (message.equals("game_go")) {
      // bot round
      turn = true;
      turnCounter--;
    } else if (message.equals("game_opp")) {
      // non-bot round
      turn = false;
      turnCounter = 2;
    } else if (message.equals("game_vrfd")) {
      bestMoves.remove(0);
      if (bestMoves.isEmpty() || bestMoves.get(0).getValue() < 0) {
        // resign
        preferredMove = "move:-1,0";
      } else {
        // next best move
        preferredMove = bestMoves.get(0).getKey();
        System.out.println("prefferedMove: " + preferredMove +
                           " priority: " + bestMoves.get(0).getValue());
        for (int i = 0; i < 5 && i < bestMoves.size(); i++) {
          System.out.println("candidate #" + i + ": " +
                             bestMoves.get(i).getKey() +
                             " priority: " + bestMoves.get(i).getValue());
        }
      }
    }
  }

  /**
   * Generate a random (but pretty good) move.
   */
  public void generateMoveList() {
    System.out.println("!! \t generating bot move...");
    priorityFunction(color);
  }

  public void priorityFunction(int playerColor) {
    try {
      bestMoves.clear();

      double disOpponent = 0;
      double disPlayer = 0;
      double priority = 0;
      for (int i = 0; i != board.getSize(); ++i) {
        for (int j = 0; j != board.getSize(); ++j) {
          disOpponent = 1 / squareDistance(i, j, -playerColor);
          disPlayer = squareDistance(i, j, playerColor) * 0.5;
          priority = disOpponent + disPlayer;

          priority += 10000;

          if ((i % 8) == 0 || (j % 8) == 0) {
            priority -= 500;
          }

          if (board.getValue(i, j) != 0) {
            System.out.println("zeroing " + i + " " + j);
            priority = Double.MIN_VALUE;
          }

          // prepare value for storage
          String move = "move:" + i + "," + j;
          Pair<String, Double> val = new Pair<String, Double>(move, priority);
          bestMoves.add(val);
          //   // put value into sorted array
          //   int index = 0;
          //   while (index < bestMoves.size() &&
          //          val.getValue() < bestMoves.get(index).getValue()) {
          //     index++;
          //   } // loop ends when val >= arr[index]. we want to insert val
          //   before
          //     // that index.
          //   bestMoves.add(index, val);
          //   // TODO : recheck my math pls loll
        }
      }
      Collections.sort(bestMoves, Collections.reverseOrder(
                                      Comparator.comparing(Pair::getValue)));
      // set best move as preferred move
      preferredMove = bestMoves.get(0).getKey();
      // preferredMove = "move:5,5";
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public double squareDistance(int x, int y, int playerColor) {
    int disSum = 0;
    for (int i = 0; i != board.getSize(); ++i) {
      for (int j = 0; j != board.getSize(); ++j) {
        if (board.board[i][j].getState() == playerColor) {
          disSum += Math.pow((i - x), 2) + Math.pow((j - y), 2);
        }
      }
    }
    return disSum;
  }

  /**
   * (unused) Generate a new move and return it.
   */
  public String getNextMessage() {
    generateMoveList();
    return preferredMove;
  }

  /**
   * Get the bot's last move.
   */
  public String getLastMessage() {
    if (bestMoves.isEmpty()) {
      // if moves havent been generated yet, do so.
      generateMoveList();
    }
    return preferredMove;
  }

  /**
   * The bot automatically responds true to game end requests.
   */
  public boolean requestConfirmation() { return true; }

  // do nothing
  public void clear() {}
}
