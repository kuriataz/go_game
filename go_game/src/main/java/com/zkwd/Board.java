package com.zkwd;

import java.util.Random;

/**
 * Stores game information: the size of the board and placement of pieces on it.
 */
public class Board {

  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  int size;
  int[][] board;

  public Board(int size) {
    this.size = size;
    this.board = new int[size][size]; // by default every cell is 0 = EMPTY
  }

  void putBlack(int x, int y) {
    if (validMove(board[x][y])) {
      board[x][y] = BLACK;
    }
  }

  void putWhite(int x, int y) {
    if (validMove(board[x][y])) {
      board[x][y] = WHITE;
    }
  }

  void removeStone(int x, int y) {
    if (validMove(board[x][y])) {
      board[x][y] = FREE;
    }
  }

  Boolean validMove(int state) {
    if (state == 0) {
      return true;
    }
    return false;
  }
}
