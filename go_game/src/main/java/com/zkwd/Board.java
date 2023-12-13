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

  int getSize() { return this.size; }

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

  Boolean validMove(int state) {
    if (state == 0) {
      return true;
    }
    return false;
  }

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
