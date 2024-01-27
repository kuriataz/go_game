package com.zkwd.server.game.players;

import com.zkwd.server.game.gamestate.Board;
import com.zkwd.server.game.gamestate.Intersection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javafx.util.Pair;

/**
 * A bot player that generates moves
 */
public class CPUPlayer implements Player {

  private static final double BASE_PRIORITY = 10000;
  // private static final double TOO_BIG = 10000000;
  private static final double MAX_PRIORITY = Double.MAX_VALUE;
  private static final double MIN_PRIORITY = 0.41;
  private static final double SUICIDE_PENALTY = -100;
  private static final double OPPONENT_DISTANCE_WEIGHT = 20;
  private static final double BOT_DISTANCE_WEIGHT = 10;
  private static final int LIBERTY_3_BONUS = 10;
  private static final int LIBERTY_2_BONUS = 5;

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

  public String getPreferredMove() { return this.preferredMove; }

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
        // preferredMove = bestMoves.get(0).getKey();
        preferredMove = samePriority(bestMoves);
        System.out.println("prefferedMove: " + preferredMove +
                           " priority: " + bestMoves.get(0).getValue());
        for (int i = 0; i < 10 && i < bestMoves.size(); i++) {
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

  private void priorityFunction(int playerColor) {
    try {
      bestMoves.clear();

      double priority = BASE_PRIORITY;
      for (int i = 0; i != board.getSize(); ++i) {
        for (int j = 0; j != board.getSize(); ++j) {
          priority = BASE_PRIORITY;

          // distance to opponetnt's stones
          priority +=
              squareDistance(i, j, -playerColor) * OPPONENT_DISTANCE_WEIGHT;
          // distance to bot's stones
          priority += squareDistance(i, j, playerColor) * BOT_DISTANCE_WEIGHT;

          if (board.board[i][j].getLiberty() == 3) {
            priority += LIBERTY_3_BONUS;
          } else if (board.board[i][j].getLiberty() == 2) {
            priority += LIBERTY_2_BONUS;
          }

          if (board.board[i][j].getState() != 0) {
            System.out.println("zeroing " + i + " " + j);
            priority = MIN_PRIORITY;
          } else {
            priority =
                checkNeighbours(board.board[i][j], -playerColor, priority);
          }
          if (priority < 0) {
            System.out.println("priority < 0 for: " + i + "," + j);
            priority = MIN_PRIORITY;
          }
          // if (priority > TOO_BIG) {
          //   priority = MAX_PRIORITY;
          // }

          // prepare value for storage
          String move = "move:" + i + "," + j;
          Pair<String, Double> val = new Pair<String, Double>(move, priority);
          bestMoves.add(val);
        }
      }
      Collections.sort(bestMoves, Collections.reverseOrder(
                                      Comparator.comparing(Pair::getValue)));
      // set best move as preferred move
      // preferredMove = bestMoves.get(0).getKey();

      // if several moves have the same priority, choose one randomly
      preferredMove = samePriority(bestMoves);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  private String samePriority(ArrayList<Pair<String, Double>> bestMoves) {

    ArrayList<String> sameMoves = new ArrayList<String>();

    double highest = bestMoves.get(0).getValue();
    int i = 0;
    while (i < bestMoves.size() && bestMoves.get(i).getValue() == highest) {
      sameMoves.add(bestMoves.get(i).getKey());
      ++i;
    }
    int index = (int)(Math.random() * sameMoves.size());

    return sameMoves.get(index);
  }

  private double checkNeighbours(Intersection potential, int playerColor,
                                 double currentPriority) {
    for (Intersection i : potential.neighbours) {
      if (i.getLiberty() == 1 && i.getState() != 0) {
        if (i.getState() == playerColor) {
          return MAX_PRIORITY;
        } else {
          return SUICIDE_PENALTY + currentPriority;
        }
      } else if (i.getLiberty() == 2 && i.getState() != 0) {
        if (i.getState() == playerColor) {
          return MAX_PRIORITY;
        } else {
          return currentPriority + SUICIDE_PENALTY / 2;
        }
      }
    }
    return currentPriority;
  }

  private double squareDistance(int x, int y, int playerColor) {
    int disSum = 0;
    for (int i = 0; i != board.getSize(); ++i) {
      for (int j = 0; j != board.getSize(); ++j) {
        if (board.board[i][j].getState() == playerColor) {
          disSum += Math.pow((i - x), 2) + Math.pow((j - y), 2);
        }
      }
    }
    if (disSum == 0) {
      return 0;
    }
    return 1.0 / disSum;
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
