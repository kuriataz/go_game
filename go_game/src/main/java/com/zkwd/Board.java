package com.zkwd;

import java.util.Random;

/**
 * Stores game information: the size of the board and placement of pieces on it.
 */
public class Board {

  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  private int size;
  private Intersection[][] board;

  public Board(int size) {
    this.size = size;
    this.board =
        new Intersection[size][size]; // by default every cell is 0 = EMPTY
  }

  public int getSize() { return this.size; }

  public int getValue(int x, int y) { return board[x][y].getState(); }

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

  Boolean validMove(int state) {
    if (state == 0) {
      return true;
    }
    return false;
  }
}
