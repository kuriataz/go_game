package com.zkwd;

import java.util.Random;
import javafx.beans.binding.When;

/**
 * Stores game information: the size of the board and placement of pieces on it.
 */
public class Board {

  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  int size;
  Intersection[][] board;

  public Board(int size) {
    this.size = size;
    this.board =
        new Intersection[size][size]; // by default every cell is 0 = EMPTY
  }

  /**
   * Randomizes the board state. Mostly for debug purposes.
   * @return The board that has been randomized (this)
   */
  public Board randomize() {
    Random r = new Random();
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        board[i][j].setState(r.nextInt() % 3 - 1);
      }
    }
    return this;
  }

  int getSize() { return this.size; }

  /**
   * Returns the state of the intersection (color)
   * @param x intersection's x coordinate
   * @param y intersection's y coordinate
   * @return state (FREE, BLACK, WHITE) of the intersection with coordinates x,
   *     y
   */
  int getValue(int x, int y) { return board[x][y].getState(); }

  void putBlack(int x, int y) {
    if (validMove(board[x][y].getState())) {
      board[x][y].setState(BLACK);
    }
  }

  void putWhite(int x, int y) {
    if (validMove(board[x][y].getState())) {
      board[x][y].setState(WHITE);
    }
  }

  void removeStone(int x, int y) { board[x][y].setState(FREE); }

  /**
   * Checks if the new stone can be put on the intersetion
   * @param state FREE, BLACK, WHITE
   * @return true if the intersetion is FREE = 0
   */
  Boolean validMove(int state) {
    if (state == 0) {
      return true;
    }
    return false;
  }

  /**
   * Sets neighbours of each intersection
   */
  void setNeighbours() {
    for (int i = 0; i != size; ++i) {
      for (int j = 0; j != size; ++j) {
        if (i + 1 < size) {
          board[i][j].neighbours.add(board[i + 1][j]);
        }
        if (i - 1 >= 0) {
          board[i][j].neighbours.add(board[i - 1][j]);
        }
        if (j + 1 < size) {
          board[i][j].neighbours.add(board[i][j + 1]);
        }
        if (j - 1 >= 0) {
          board[i][j].neighbours.add(board[i][j - 1]);
        }
      }
    }
  }
}
