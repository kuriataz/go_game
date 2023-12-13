package com.zkwd.server;

import java.util.Random;

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
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        board[i][j] = new Intersection(0);
      }
    }
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

  /**
   * Cycle between values (debug).
   * @param x row
   * @param y column
  */
  public void flip(int x, int y){
    board[y][x].setState((board[x][y].getState() + 2) % 3 - 1);
  }

  /**
   * Gets the size of the board.
   * @return The size of the board
   */
  int getSize() { return this.size; }

  /**
   * Returns the state of the intersection (color).
   * @param x intersection's x coordinate
   * @param y intersection's y coordinate
   * @return state (0: free, 1: white, -1: black) of the intersection with coordinates x,
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
   * Checks if the new stone can be put on the intersetion.
   * @param state FREE, BLACK, WHITE
   * @return true if the intersetion is FREE = 0
   */
  Boolean validMove(int state) {
    return (state == FREE);
  }

  /**
   * Sets neighbours of each intersection.
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

  /**
   * FORMATTING OF THE OUTPUT STRING
   * white -> W
   * black -> B
   * empty -> E
   * printed column by column, columns divided by |
   */
  public String prepareBoardString() {
    String out = "";
    for(int i = 0; i < size; ++i){
      for(int j = 0; j < size; ++j){
        int k = board[i][j].getState();
        out += (k == 1) ? "W" : (k == -1) ? "B" : "E";
      }
      out += "|";
    }
    return out;
  }
}
